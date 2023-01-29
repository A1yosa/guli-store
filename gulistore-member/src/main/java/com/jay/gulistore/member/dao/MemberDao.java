package com.jay.gulistore.member.dao;


import com.jay.gulistore.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 17:28:50
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
