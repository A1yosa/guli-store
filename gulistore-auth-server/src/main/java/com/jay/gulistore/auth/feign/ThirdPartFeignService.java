package com.jay.gulistore.auth.feign;

import com.jay.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>Title: ThirdPartFeignService</p>
 * Description：
 * date：2020/6/25 15:01
 */
@FeignClient("gulistore-thirdp")
public interface ThirdPartFeignService {

    @GetMapping("/sms/sendcode")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}