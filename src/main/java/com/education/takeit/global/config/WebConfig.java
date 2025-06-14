package com.education.takeit.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig implements WebMvcConfigurer {

  @Value("${client.base-url}")
  private String baseUrl;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**") // 모든 API 경로에 대해
        .allowedOrigins(baseUrl)
        .allowedMethods("*") // 모든 HTTP 메서드 허용
        .allowedHeaders("*") // 모든 헤더 허용
        .exposedHeaders("Authorization")
        .allowCredentials(true); // 인증정보 포함
  }
}
