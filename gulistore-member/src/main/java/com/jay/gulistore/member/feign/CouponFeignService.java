package com.jay.gulistore.member.feign;


import com.jay.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;


/*
* 这是一个声明式的远程调用服务
* */
@FeignClient("gulistore-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupons();

}
