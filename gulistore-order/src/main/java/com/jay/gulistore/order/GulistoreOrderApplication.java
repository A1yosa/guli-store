package com.jay.gulistore.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

//test

@EnableDiscoveryClient
@SpringBootApplication
public class GulistoreOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulistoreOrderApplication.class, args);
    }

}
