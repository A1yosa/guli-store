package com.jay.gulistore.coupon.dao;

import com.jay.gulistore.coupon.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 17:05:22
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
