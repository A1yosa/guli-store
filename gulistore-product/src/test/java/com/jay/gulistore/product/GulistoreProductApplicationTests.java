package com.jay.gulistore.product;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.jay.gulistore.product.entity.BrandEntity;
import com.jay.gulistore.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/*
 * 1、引入oss-starter
 * 2、配置key、endpoint相关信息
 * 3、使用OSSClient进行相关操作
 * */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GulistoreProductApplication.class)
class GulistoreProductApplicationTests {

    @Autowired
    BrandService brandService;


    /*放弃此测试功能

    @Autowired
    OSSClient ossClient;

    @Test
    public void testUpload() throws FileNotFoundException {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = "oss-cn-hangzhou.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = "LTAI5tNfd6cAMvkfyGvmE8o4";
        String accessKeySecret = "bc0GcCt7YwdGqiFPYQhF2EeZnm5Q4F";
        //         填写Bucket名称，例如examplebucket。
        String bucketName = "skdstore";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "qw.jpg";
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        String filePath = "C:\\Users\\YELLOW\\Documents\\Tencent Files\\279368894\\FileRecv\\MobileFile\\qw.jpg";
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);


        InputStream inputStream = new FileInputStream(filePath);
        // 创建PutObjectRequest对象。
        ossClient.putObject(bucketName, objectName, inputStream);

        ossClient.shutdown();

        System.out.println("上传成功!");
    }*/

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
        list.forEach((item) -> {
            System.out.println(item);
        });
    }

}
