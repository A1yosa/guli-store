package com.jay.gulistore.order.service.impl;

import com.jay.common.vo.MemberRespVo;
import com.jay.gulistore.order.feign.CartFeignService;
import com.jay.gulistore.order.feign.MemberFeignService;
import com.jay.gulistore.order.interceptor.LoginUserInterceptor;
import com.jay.gulistore.order.vo.MemberAddressVo;
import com.jay.gulistore.order.vo.OrderConfirmVo;
import com.jay.gulistore.order.vo.OrderItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jay.common.utils.PageUtils;
import com.jay.common.utils.Query;

import com.jay.gulistore.order.dao.OrderDao;
import com.jay.gulistore.order.entity.OrderEntity;
import com.jay.gulistore.order.service.OrderService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    ThreadPoolExecutor executor;

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
        CompletableFuture.allOf(getAddressFuture,cartFuture).get();
        return confirmVo;
    }



}