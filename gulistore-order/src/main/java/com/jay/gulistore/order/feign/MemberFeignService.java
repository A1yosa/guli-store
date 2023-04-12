package com.jay.gulistore.order.feign;

import com.jay.gulistore.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("gulistore-member")
public interface MemberFeignService {

    /**
    * 返回会员所有的收货地址列表
     * @param memberId 会员ID
     * @return
     */

    @GetMapping("/member/memberreceiveaddress/{memberId}/address")
    List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);

}
