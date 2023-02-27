package com.jay.gulistore.product.web;


import com.jay.gulistore.product.entity.CategoryEntity;
import com.jay.gulistore.product.service.CategoryService;
import com.jay.gulistore.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;
    @GetMapping({"/", "index.html"})
    public String indexPage(Model model) {
        //todo 1、查出所有的一级分类
        List<CategoryEntity> categoryEntities=categoryService.getLevel1Category();
        model.addAttribute("categorys",categoryEntities);
        return "index";
    }


    @ResponseBody
    @GetMapping("/index/catelog.json")
    public Map<String, List<Catelog2Vo>> getCatelogJson(){
        Map<String, List<Catelog2Vo>> catelogJson = categoryService.getCatelogJson();
        return catelogJson;
    }
}
