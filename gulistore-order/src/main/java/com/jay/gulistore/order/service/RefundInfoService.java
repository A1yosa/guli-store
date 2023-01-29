package com.jay.gulistore.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jay.common.utils.PageUtils;
import com.jay.gulistore.order.entity.RefundInfoEntity;

import java.util.Map;


/**
 * 退款信息
 *
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 17:44:30
 */
public interface RefundInfoService extends IService<RefundInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

