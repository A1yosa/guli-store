package com.jay.gulistore.order.dao;

import com.jay.gulistore.order.entity.OrderReturnReasonEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;


/**
 * 退货原因
 * 
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 17:44:30
 */
@Mapper
public interface OrderReturnReasonDao extends BaseMapper<OrderReturnReasonEntity> {
	
}
