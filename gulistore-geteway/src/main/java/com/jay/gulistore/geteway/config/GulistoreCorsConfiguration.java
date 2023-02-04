package com.jay.gulistore.geteway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GulistoreCorsConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfigurationSource configurationSource = new CorsConfigurationSource() {
//            @Override
//            public org.springframework.web.cors.CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
//                return null;
//            }
//        }
        CorsConfiguration corsConfiguration = new CorsConfiguration();

//        配置跨域
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
//        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOriginPattern("*");

        source.registerCorsConfiguration("/**",corsConfiguration);


        return new CorsWebFilter(source);
    }
}
