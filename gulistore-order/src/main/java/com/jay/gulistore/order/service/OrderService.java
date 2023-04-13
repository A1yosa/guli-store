package com.jay.gulistore.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jay.common.utils.PageUtils;
import com.jay.gulistore.order.entity.OrderEntity;
import com.jay.gulistore.order.vo.OrderConfirmVo;
import com.jay.gulistore.order.vo.OrderSubmitVo;
import com.jay.gulistore.order.vo.PayVo;
import com.jay.gulistore.order.vo.SubmitOrderResponseVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * 订单
 *
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 17:44:30
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    PayVo getOrderPay(String orderSn);

    OrderEntity getOrderByOrderSn(String orderSn);


    PageUtils queryPageWithItem(Map<String, Object> params);

}

