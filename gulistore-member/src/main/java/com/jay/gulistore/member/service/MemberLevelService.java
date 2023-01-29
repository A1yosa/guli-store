package com.jay.gulistore.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jay.common.utils.PageUtils;
import com.jay.gulistore.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 17:28:50
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

