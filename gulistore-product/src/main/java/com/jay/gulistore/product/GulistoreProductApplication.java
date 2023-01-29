package com.jay.gulistore.product;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@MapperScan("com.jay.gulistore.product.dao")
@SpringBootApplication
public class GulistoreProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulistoreProductApplication.class, args);
    }

}
