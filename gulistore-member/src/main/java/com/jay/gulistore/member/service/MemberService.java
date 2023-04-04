package com.jay.gulistore.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jay.common.utils.PageUtils;
import com.jay.gulistore.member.entity.MemberEntity;
import com.jay.gulistore.member.exception.PhoneExistException;
import com.jay.gulistore.member.exception.UserNameExistException;
import com.jay.gulistore.member.vo.MemberLoginVo;
import com.jay.gulistore.member.vo.SocialUser;
import com.jay.gulistore.member.vo.UserRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 17:28:50
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(UserRegisterVo userRegisterVo);

    void checkPhone(String phone) throws PhoneExistException;

    void checkUserName(String username) throws UserNameExistException;

    /**
     * 普通登录
     */
    MemberEntity login(MemberLoginVo vo);

    /**
     * 社交登录
     */
    MemberEntity login(SocialUser socialUser);

}

