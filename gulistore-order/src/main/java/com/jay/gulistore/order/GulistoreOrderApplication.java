package com.jay.gulistore.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

//test

@EnableFeignClients
@EnableRedisHttpSession
@EnableDiscoveryClient
@SpringBootApplication
public class GulistoreOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulistoreOrderApplication.class, args);
    }

}
