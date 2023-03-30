package com.jay.gulistore.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class GulistoreAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulistoreAuthServerApplication.class, args);
    }

}
