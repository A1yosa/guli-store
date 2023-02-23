package com.jay.gulistore.ware;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//test

@EnableTransactionManagement
@MapperScan("com.jay.gulistore.ware.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class GulistoreWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulistoreWareApplication.class, args);
    }

}
