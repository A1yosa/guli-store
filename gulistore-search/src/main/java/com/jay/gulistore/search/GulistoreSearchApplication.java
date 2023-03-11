package com.jay.gulistore.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//@SpringBootApplication(scanBasePackages="com.jay.gulistore.search.controller",exclude = DataSourceAutoConfiguration.class)
public class GulistoreSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulistoreSearchApplication.class, args);
    }

}
