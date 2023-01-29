package com.jay.gulistore.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jay.common.utils.PageUtils;
import com.jay.gulistore.product.entity.CategoryBrandRelationEntity;

import java.util.Map;


/**
 * 品牌分类关联
 *
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 14:25:14
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

