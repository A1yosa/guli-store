package com.jay.gulistore.search.controller;

import com.jay.gulistore.search.service.MallSearchService;
import com.jay.gulistore.search.vo.SearchParam;
import com.jay.gulistore.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


//@ResponseBody
//@RequestMapping
//@RestController
@Controller
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam param , Model model){
        SearchResult result = mallSearchService.search(param);
        System.out.println("===================="+result);
        model.addAttribute("result",result);
        return "list";

    }
}
