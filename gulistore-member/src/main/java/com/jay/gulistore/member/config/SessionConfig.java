package com.jay.gulistore.member.config;


import com.jay.common.constant.AuthServerConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Description：设置Session作用域、自定义cookie序列化机制
 */
@EnableRedisHttpSession
@Configuration
public class SessionConfig {

    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        // 明确的指定Cookie的作用域
        cookieSerializer.setDomainName("sukestore.com");
        cookieSerializer.setCookieName(AuthServerConstant.SESSION);
        return cookieSerializer;
    }

    /**
     * 自定义序列化机制
     * 这里方法名必须是：springSessionDefaultRedisSerializer
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }
}

