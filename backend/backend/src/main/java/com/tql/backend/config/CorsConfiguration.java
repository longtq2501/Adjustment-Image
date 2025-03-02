package com.tql.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET", "PUT", "POST", "DELETE", "OPTIONS") // Thêm OPTIONS
                        .allowedOrigins("http://localhost:3000") // Bỏ dấu / cuối
                        .allowedHeaders("*") // Cho phép tất cả headers
                        .exposedHeaders("Authorization") // Hiển thị Authorization header
                        .allowCredentials(true); // Cho phép credentials
            }
        };
    }
}