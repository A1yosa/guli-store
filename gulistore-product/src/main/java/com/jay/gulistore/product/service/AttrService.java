package com.jay.gulistore.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jay.common.utils.PageUtils;
import com.jay.gulistore.product.entity.AttrEntity;

import java.util.Map;


/**
 * 商品属性
 *
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 14:25:14
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

