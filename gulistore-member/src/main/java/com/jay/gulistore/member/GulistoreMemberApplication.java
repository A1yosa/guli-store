package com.jay.gulistore.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/*
* 1、远程调用别的服务
*
*
*   引用open-feign
*   编写一个接口，告诉springCloud这个接口需要远程调用服务
*       声明接口的每一个方法都是调用那个远程服务的那个请求
*   开启远程调用功能
* test
* */

//@ComponentScan("com.jay.gulistore.member.feign")
@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.jay.gulistore.member.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class GulistoreMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulistoreMemberApplication.class, args);
    }

}
