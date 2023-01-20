package com.jay.gulistore.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.jay.gulistore.product.entity.BrandEntity;
import com.jay.gulistore.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = GulistoreProductApplication.class)
class GulistoreProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Test
    void contextLoads() {

        BrandEntity brandEntity = new BrandEntity();

        //测试插入
//        brandEntity.setDescript("");
//        brandEntity.setName("菠萝");
//
//        brandService.save(brandEntity);
//
//        System.out.printf("保存成功！");

        //测试更新
//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("这是描述");
//
//        brandService.updateById(brandEntity);

        //测试查询
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 6L));
        list.forEach((item)->{
            System.out.println(item);
        });
    }

}
