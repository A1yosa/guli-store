package com.jay.gulistore.product;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//test
/*
* 逻辑删除
* 1、配置全局的逻辑删除规则（可省略）
* 2、配置逻辑删除的组件Bean（可省略）
* 3、给Bean加上逻辑删除注解@TableLogic
*
* JSR303
* 1、给bean添加校验注解：javax.valitation.constrains,并定义自己的massage提示
* 2、开启校验功能@Valid, 校验错误会有默认的响应
* 3、给校验的bean后加上BindingResult即可拿到校验结果
* 4、分组校验 略
* 5、自定义校验
*       编写一个自定义的校验注解
*       编写一个自定义的校验器
*       关联自定义的校验器和自定义的校验注解（可以指定多个不同的校验器，适配不同类型的校验）
*
*
* 统一的异常处理@RestControllerAdvice
* 1、编写异常处理类，使用@RestControllerAdvice
* 2、使用@ExceptionHandler标注方法可以处理异常
*
* */

@EnableRedisHttpSession
@EnableCaching
@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.jay.gulistore.product.fegin")
@EnableDiscoveryClient
@MapperScan("com.jay.gulistore.product.dao")
@SpringBootApplication
public class GulistoreProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulistoreProductApplication.class, args);
    }

}
