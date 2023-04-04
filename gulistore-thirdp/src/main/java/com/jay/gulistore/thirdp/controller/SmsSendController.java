package com.jay.gulistore.thirdp.controller;



import com.jay.common.exception.BizCodeEnume;
import com.jay.common.utils.R;
import com.jay.gulistore.thirdp.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>Title: SmsSendController</p>
 * Description：
 * date：2020/6/25 14:53
 */
@Controller
@RequestMapping("/sms")
public class SmsSendController {

    @Autowired
    SmsComponent smsComponent;

    /*** 提供给别的服务进行调用的
     */
    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code){
//        if(!"fail".equals(smsComponent.sendSmsCode(phone, code).split("_")[0])){
//            return R.ok();
//        }
//        return R.error(BizCodeEnume.SMS_SEND_CODE_EXCEPTION.getCode(), BizCodeEnume.SMS_SEND_CODE_EXCEPTION.getMsg());

        smsComponent.sendSmsCode(phone,code);
        return R.ok();
    }
}

