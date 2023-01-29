package com.jay.gulistore.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jay.common.utils.PageUtils;
import com.jay.gulistore.order.entity.OrderOperateHistoryEntity;

import java.util.Map;


/**
 * 订单操作历史记录
 *
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 17:44:30
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

