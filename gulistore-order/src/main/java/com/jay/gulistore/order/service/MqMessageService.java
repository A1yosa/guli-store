package com.jay.gulistore.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jay.common.utils.PageUtils;
import com.jay.gulistore.order.entity.MqMessageEntity;

import java.util.Map;

/**
 *
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 17:44:30
 */
public interface MqMessageService extends IService<MqMessageEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

