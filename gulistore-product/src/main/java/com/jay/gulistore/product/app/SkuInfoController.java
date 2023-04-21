package com.jay.gulistore.product.app;

import java.util.*;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.jay.gulistore.product.entity.SkuInfoEntity;
import com.jay.gulistore.product.service.SkuInfoService;
import com.jay.common.utils.PageUtils;
import com.jay.common.utils.R;

import javax.servlet.http.HttpServletRequest;


/**
 * sku信息
 *
 * @author jay
 * @email huanchen659@gmail.com
 * @date 2023-01-20 14:25:14
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;


    /**
     * 获取指定商品的价格
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}/price")
    public R getPrice(@PathVariable("skuId") Long skuId){
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        return R.ok().setData(skuInfoEntity.getPrice().toString());
    }

    @GetMapping("/recommend.html")
    public R listPage( Long skuId , Model model, HttpServletRequest request){
        Random r = new Random();
        List<SkuInfoEntity> recommendRes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            skuId = Long.valueOf(r.nextInt(200));
            SkuInfoEntity recommend = skuInfoService.getById(skuId);
            recommendRes.add(recommend);
        }
//        skuId = Long.valueOf(r.nextInt(200));
//        SkuInfoEntity recommend = skuInfoService.getById(skuId);
        System.out.println("===================="+recommendRes);
//        model.addAttribute("recommendImag",recommend.getSkuDefaultImg());
//        model.addAttribute("recommendTitle",recommend.getSkuTitle());
//        model.addAttribute("recommendPrice",recommend.getPrice());
        model.addAttribute("recommendRes",recommendRes);
        return R.ok().put("recommendRes",recommendRes);

    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:skuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    //@RequiresPermissions("product:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:skuinfo:save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:skuinfo:update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:skuinfo:delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}
