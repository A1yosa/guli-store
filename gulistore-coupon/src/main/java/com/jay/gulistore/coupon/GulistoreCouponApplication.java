package com.jay.gulistore.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/*
* 如何使用nacos作为配置中心统一管理配置
*   引入依赖
*       <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>
*   创建一个bootstrap.properties，内容为：
*           spring.application.name=gulistore-coupon

            spring.cloud.nacos.config.server-addr=127.0.0.1:8848
*   给配置中心添加数据集（Data Id）gulistore-coupon.properties（应用名.properties）,进行编辑，简化操作
*   动态获取并刷新配置
*           @RefreshScope
*           @Value("${配置项的名}")
*           注意：优先使用配置中心的 配置
*
* 细节
*       命名空间：
*           默认为public（保留空间，默认新增的所有配置都在）
*           1、开发、测试、生产:利用命名空间来做环境隔离
*           注意：在bootstrap.properties指明使用哪个命名空间下的配置（十六进制ID）
*           2、每个微服务之间互相隔离配置，每个微服务都创建自己的命名空间，只加载自己命名空间下的所有配置
*
*       配置集：所有配置的集合
*       配置集ID：类似于配置文件名（Data Id）
*       配置分组：
*           m默认所有的配置集都属于：DEfault——Group
*
*   每个微服务创建自己的命名空间，使用配置分组区分环境，dev，test，prod
*
*
* 同时加载多个配置集
*   微服务任何配置信息、任何配置文件都可以放在配置中心中
*   只需在bootstrap.properties中说明加载配置中心中哪些配置文件即可
*   @Value  @ConfigurationProperties....以前spring boot任何方法从配置文件中取值，都能使用，且优先使用配置中心
*
*test
* */


@EnableDiscoveryClient
@SpringBootApplication
public class GulistoreCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulistoreCouponApplication.class, args);
    }

}
