package com.jay.gulistore.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class GulistoreWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulistoreWareApplication.class, args);
    }

}
