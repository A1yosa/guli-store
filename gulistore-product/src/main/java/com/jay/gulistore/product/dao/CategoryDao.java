package com.jay.gulistore.product.dao;

import com.jay.gulistore.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;


/**
 * 商品三级分类
 * 
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 14:25:14
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
