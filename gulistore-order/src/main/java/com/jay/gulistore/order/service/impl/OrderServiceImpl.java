package com.jay.gulistore.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.jay.common.utils.R;
import com.jay.common.vo.MemberRespVo;
import com.jay.gulistore.order.dao.OrderItemDao;
import com.jay.gulistore.order.entity.OrderItemEntity;
import com.jay.gulistore.order.enume.OrderStatusEnum;
import com.jay.gulistore.order.feign.CartFeignService;
import com.jay.gulistore.order.feign.MemberFeignService;
import com.jay.gulistore.order.feign.ProductFeignService;
import com.jay.gulistore.order.feign.WareFeignService;
import com.jay.gulistore.order.interceptor.LoginUserInterceptor;
import com.jay.gulistore.order.service.OrderItemService;
import com.jay.gulistore.order.to.OrderCreateTo;
import com.jay.gulistore.order.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jay.common.utils.PageUtils;
import com.jay.common.utils.Query;

import com.jay.gulistore.order.dao.OrderDao;
import com.jay.gulistore.order.entity.OrderEntity;
import com.jay.gulistore.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();
    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    CartFeignService cartFeignService;
    @Autowired
    WareFeignService wareFeignService;
    //    @Autowired
//    StringRedisTemplate redisTemplate;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

//    @Override
//    public OrderConfirmVo confirmOrder() {
//        OrderConfirmVo confirmVo = new OrderConfirmVo();
//        MemberRespVo memberRespVo = LoginUserInterceptor.threadLocal.get();
//        // 1、远程查询所有的地址列表
//        List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
//        confirmVo.setAddressVos(address);
//        // 2、远程查询购物车所有选中的购物项
//        List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
//        confirmVo.setItems(items);
//        // 3、查询用户积分
//        Integer integration = memberRespVo.getIntegration();
//        confirmVo.setIntegration(integration);
//        // 4、其他数据自动计算
//        // 5、防重令牌
//        return confirmVo;
//    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        // 获取主线程的域，主线程中获取请求头信息
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 1、远程查询所有的地址列表
        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            // 将主线程的域放在该线程的域中
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setAddress(address);
        }, executor);

        // 2、远程查询购物车所有选中的购物项
        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            // 将老请求的域放在该线程的域中
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(items);
        }, executor);
        // feign在远程调用请求之前要构造
        // 3、查询用户积分
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegration(integration);
        // 4、其他数据自动计算
        // TODO 5、防重令牌
        CompletableFuture.allOf(getAddressFuture, cartFuture).get();
        return confirmVo;
    }

    /**
     * 下单操作：验令牌、创建订单、验价格、验库存
     *
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        // 在当前线程共享 OrderSubmitVo
        confirmVoThreadLocal.set(vo);
        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
        // 从拦截器中拿到当前的用户
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        response.setCode(0);
        // 1、验证令牌【令牌的对比和删除必须保证原子性】,通过使用脚本来完成(0：令牌校验失败; 1: 删除成功)
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        // 令牌验证成功
        // 2、创建订单、订单项等信息
        OrderCreateTo order = createOrder();
        // 3、验价
        BigDecimal payAmount = order.getOrder().getPayAmount();
        BigDecimal payPrice = vo.getPayPrice();
        if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
            // 金额对比成功
            // 4、保存订单;
            saveOrder(order);
            order.getOrder().getOrderSn();
            // 5、库存锁定，只要有异常回滚订单数据
            // 订单号，所有订单项(skuId,skuName,num)
            List<OrderItemVo> locks = order.getOrderItems().stream().map(item -> {
                OrderItemVo itemVo = new OrderItemVo();
                itemVo.setSkuId(item.getSkuId());
                itemVo.setCount(item.getSkuQuantity());
                itemVo.setTitle(item.getSkuName());
                return itemVo;
            }).collect(Collectors.toList());

            response.setOrder(order.getOrder());
            return response;
        }else {
            return null;
        }
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity order = this.getOrderByOrderSn(orderSn);
        // 保留2位小数位向上补齐
        payVo.setTotal_amount(order.getTotalAmount().add(order.getFreightAmount() == null ? new BigDecimal("0") : order.getFreightAmount()).setScale(2, BigDecimal.ROUND_UP).toString());
        payVo.setOut_trade_no(order.getOrderSn());
        List<OrderItemEntity> entities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
        payVo.setSubject("skstore");
        payVo.setBody("skstore");
        if (null != entities.get(0).getSkuName() && entities.get(0).getSkuName().length() > 1) {
//			payVo.setSubject(entities.get(0).getSkuName());
//			payVo.setBody(entities.get(0).getSkuName());
            payVo.setSubject("skstore");
            payVo.setBody("skstore");
        }
        return payVo;
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        return this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberRespVo respVo = LoginUserInterceptor.loginUser.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),

                // 查询这个用户的最新订单 [降序排序]
                new QueryWrapper<OrderEntity>().eq("member_id", respVo.getId()).orderByDesc("id")
        );
        List<OrderEntity> order_sn = page.getRecords().stream().map(order -> {
            // 查询这个订单关联的所有订单项
            List<OrderItemEntity> orderSn = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItemEntities(orderSn);
            return order;
        }).collect(Collectors.toList());
        page.setRecords(order_sn);
        return new PageUtils(page);
    }

    /**
     * 保存订单所有数据
     */
    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);

        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItems = orderItems.stream().map(item -> {
            item.setOrderId(orderEntity.getId());
            item.setSpuName(item.getSpuName());
            item.setOrderSn(order.getOrder().getOrderSn());
            return item;
        }).collect(Collectors.toList());
        orderItemService.saveBatch(orderItems);
    }


//    @Override
//    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
//        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
//        // 从拦截器中拿到当前的用户
//        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
//        // 1、验证令牌【令牌的对比和删除必须保证原子性】,通过使用脚本来完成(0：令牌校验失败; 1: 删除成功)
//        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//
//        return response;
//    }


    /**
     * 创建订单、订单项等信息
     *
     * @return
     */
    private OrderCreateTo createOrder() {
        OrderCreateTo createTo = new OrderCreateTo();
        // 1、生成一个订单号
        String orderSn = IdWorker.getTimeId();
        // 2、构建一个订单
        OrderEntity orderEntity = buildOrder(orderSn);
        // 3、获取到所有的订单项
        List<OrderItemEntity> itemEntities = buildOrderItems(orderSn);
        // 4、计算价格、积分等相关信息
        computePrice(orderEntity, itemEntities);

        createTo.setOrder(orderEntity);
        createTo.setOrderItems(itemEntities);
        return createTo;
    }

    /**
     * 计算价格
     *
     * @param orderEntity
     * @param itemEntities
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> itemEntities) {
        BigDecimal total = new BigDecimal("0.0");
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");
        BigDecimal gift = new BigDecimal("0.0");
        BigDecimal growth = new BigDecimal("0.0");
        // 1、订单的总额，叠加每一个订单项的总额信息
        for (OrderItemEntity entity : itemEntities) {
            total = total.add(entity.getRealAmount());
            coupon = coupon.add(entity.getCouponAmount());
            integration = integration.add(entity.getIntegrationAmount());
            promotion = promotion.add(entity.getPromotionAmount());
            gift = gift.add(new BigDecimal(entity.getGiftIntegration()));
            growth = growth.add(new BigDecimal(entity.getGiftGrowth()));
        }
        // 订单总额
        orderEntity.setTotalAmount(total);
        // 应付总额
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setCouponAmount(coupon);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setPromotionAmount(promotion);
        // 设置积分等信息
        orderEntity.setIntegration(gift.intValue());
        orderEntity.setGrowth(growth.intValue());
        orderEntity.setDeleteStatus(0);//0 未删除
    }


    /**
     * 构建订单
     *
     * @param orderSn
     * @return
     */
    private OrderEntity buildOrder(String orderSn) {
        MemberRespVo respVp = LoginUserInterceptor.loginUser.get();
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(orderSn);
        entity.setMemberId(respVp.getId());

        OrderSubmitVo submitVo = confirmVoThreadLocal.get();
        // 1、获取运费 和 收货信息
        R fare = wareFeignService.getFare(submitVo.getAddrId());
        FareVo fareResp = fare.getData(new TypeReference<FareVo>() {
        });
        // 2、设置运费
        entity.setFreightAmount(fareResp.getFare());
        // 3、设置收货人信息
        entity.setReceiverPostCode(fareResp.getAddress().getPostCode());
        entity.setReceiverProvince(fareResp.getAddress().getProvince());
        entity.setReceiverRegion(fareResp.getAddress().getRegion());
        entity.setReceiverCity(fareResp.getAddress().getCity());
        entity.setReceiverDetailAddress(fareResp.getAddress().getDetailAddress());
        entity.setReceiverName(fareResp.getAddress().getName());
        entity.setReceiverPhone(fareResp.getAddress().getPhone());
        // 4、设置订单的相关状态信息
        entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        // 5、默认取消信息
        entity.setAutoConfirmDay(7);
        return entity;
    }


    /**
     * 构建所有订单项数据
     *
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        // 最后确定每个购物项的价格
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        if (currentUserCartItems != null && currentUserCartItems.size() > 0) {
            List<OrderItemEntity> itemEntities = currentUserCartItems.stream().map(cartItem -> {
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
            return itemEntities;
        }
        return null;
    }

    /**
     * 构建某一个订单项
     *
     * @param cartItem
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        // 1、订单信息：订单号 v
        // 2、商品的spu信息
        Long skuId = cartItem.getSkuId();
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {
        });
        itemEntity.setSpuId(data.getId());
        itemEntity.setSpuBrand(data.getBrandId().toString());
        itemEntity.setSpuName(data.getSpuName());
        itemEntity.setCategoryId(data.getCatelogId());
        // 3、商品的sku信息  v
        itemEntity.setSkuId(cartItem.getSkuId());
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPic(cartItem.getImage());
        itemEntity.setSkuPrice(cartItem.getPrice());
        itemEntity.setSkuQuantity(cartItem.getCount());
        itemEntity.setSkuAttrsVals(StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";"));
        // 4、优惠信息【不做】
        // 5、积分信息
        itemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        itemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        // 6、订单项的价格信息
        itemEntity.setPromotionAmount(new BigDecimal("0"));
        itemEntity.setCouponAmount(new BigDecimal("0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0"));
        // 当前订单项的实际金额 总额-各种优惠
        BigDecimal orign = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        BigDecimal subtract = orign.subtract(itemEntity.getCouponAmount()).
                subtract(itemEntity.getCouponAmount()).
                subtract(itemEntity.getIntegrationAmount());
        itemEntity.setRealAmount(subtract);
        return itemEntity;
    }


}