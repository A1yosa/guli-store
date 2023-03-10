package com.jay.gulistore.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jay.common.utils.PageUtils;
import com.jay.gulistore.member.entity.MemberStatisticsInfoEntity;

import java.util.Map;

/**
 * 会员统计信息
 *
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 17:28:50
 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

