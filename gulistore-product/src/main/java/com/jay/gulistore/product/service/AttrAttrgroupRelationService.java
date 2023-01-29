package com.jay.gulistore.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jay.common.utils.PageUtils;
import com.jay.gulistore.product.entity.AttrAttrgroupRelationEntity;

import java.util.Map;


/**
 * 属性&属性分组关联
 *
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 14:25:14
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

