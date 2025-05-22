package com.education.takeit.global.config;

import com.google.api.client.util.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class webClientConfig {
    @Value("${fastapi.base-url}")
    private String fastapiBaseUrl;

    @Bean
    public WebClient fastapiWebClient(){
        return WebClient.builder()
                .baseUrl(fastapiBaseUrl) // Content-Type:application/json 헤더 추기
                .defaultHeader(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
