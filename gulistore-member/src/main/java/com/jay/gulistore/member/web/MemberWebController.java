package com.jay.gulistore.member.web;


import com.jay.common.utils.R;
import com.jay.gulistore.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

/**
 * <p>Title: MemberWebController</p>
 */
@Controller
public class MemberWebController {

    @Autowired
    private OrderFeignService orderFeignService;

    @GetMapping("memberOrder.html")
    public String memberOrderPage(
            @RequestParam(value = "pageNum", required = false, defaultValue = "1") String pageNum,
            Model model) {

        // 这里可以获取到支付宝给我们传来的所有数据
        // 查出当前登录用户的所有订单
        HashMap<String, Object> page = new HashMap<>();
        page.put("page", pageNum);
        R r = orderFeignService.listWithItem(page);
        model.addAttribute("orders", r);
//		支付宝返回的页面数据
//		System.out.println(r.get("page"));
        return "orderList";
    }

    @RequestMapping("/submit")
    public String submitForm(@RequestParam("rating") int rating) {
        // 处理评分值和其他参数
        return "success";
    }
}

