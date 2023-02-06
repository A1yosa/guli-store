package com.jay.gulistore.product;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

//test
/*
* 逻辑删除
* 1、配置全局的逻辑删除规则（可省略）
* 2、配置逻辑删除的组件Bean（可省略）
* 3、给Bean加上逻辑删除注解@TableLogic
* */

@EnableDiscoveryClient
@MapperScan("com.jay.gulistore.product.dao")
@SpringBootApplication
public class GulistoreProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulistoreProductApplication.class, args);
    }

}
