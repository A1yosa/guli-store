package com.jay.gulistore.member.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.jay.common.exception.BizCodeEnume;
import com.jay.gulistore.member.exception.PhoneExistException;
import com.jay.gulistore.member.exception.UserNameExistException;
import com.jay.gulistore.member.feign.CouponFeignService;
import com.jay.gulistore.member.vo.MemberLoginVo;
import com.jay.gulistore.member.vo.SocialUser;
import com.jay.gulistore.member.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jay.gulistore.member.entity.MemberEntity;
import com.jay.gulistore.member.service.MemberService;
import com.jay.common.utils.PageUtils;
import com.jay.common.utils.R;



/**
 * 会员
 *
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 17:28:50
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;


    @Autowired
    CouponFeignService couponFeignService;


    @RequestMapping("/coupons")
    public R test(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("大春");

        R membercoupons = couponFeignService.membercoupons();


        return R.ok().put("member",memberEntity).put("coupons",membercoupons.get("coupons"));
    }

    @PostMapping("/oauth2/login")
    public R login(@RequestBody SocialUser socialUser){

        MemberEntity memberEntity = memberService.login(socialUser);
        if(memberEntity != null){
            return R.ok().setData(memberEntity);
        }else {
            return R.error(BizCodeEnume.SOCIALUSER_LOGIN_ERROR.getCode(), BizCodeEnume.SOCIALUSER_LOGIN_ERROR.getMsg());
        }
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo){

        MemberEntity memberEntity = memberService.login(vo);
        if(memberEntity != null){
            return R.ok().setData(memberEntity);
        }else {
            return R.error(BizCodeEnume.LOGINACTT_PASSWORD_ERROR.getCode(), BizCodeEnume.LOGINACTT_PASSWORD_ERROR.getMsg());
        }
    }

    @PostMapping("/register")
    public R register(@RequestBody UserRegisterVo userRegisterVo){

        try {
            memberService.register(userRegisterVo);
        } catch (PhoneExistException e) {
            return R.error(BizCodeEnume.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnume.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UserNameExistException e) {
            return R.error(BizCodeEnume.USER_EXIST_EXCEPTION.getCode(), BizCodeEnume.USER_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
