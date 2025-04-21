package com.education.takeit.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // H2 콘솔 iframe 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**","/oauth2/**","/login/**").permitAll() // H2 콘솔 허용
                        .anyRequest().permitAll() // 나머지는 일단 전부 허용 (테스트용)
                )
                .oauth2Login(Customizer.withDefaults())
                .build();


    }
}
