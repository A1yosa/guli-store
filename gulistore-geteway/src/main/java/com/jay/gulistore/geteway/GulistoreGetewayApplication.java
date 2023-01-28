package com.jay.gulistore.geteway;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/*
* 开启服务的注册发现(配置nacos的注册中心地址)
* 注意在pom.xml中的common 依赖中exclusion spring-webmvc
* */

@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
public class GulistoreGetewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulistoreGetewayApplication.class, args);
    }

}
