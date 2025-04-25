package com.education.takeit.global.config;

import com.education.takeit.global.security.JwtAuthenticationFilter;
import com.education.takeit.global.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final String[] swaggerPath = {"/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/api-docs/**", "/swagger-ui.html", "/error"};

    private final JwtUtils jwtUtils;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))  // H2 콘솔을 사용할 경우
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(swaggerPath).permitAll()  // Swagger 관련 경로 허용
                        .requestMatchers(
                                "/api/user/signin",  
                                "/api/user/signup",  
                                "/h2-console/**",     // H2 콘솔 허용
                                "/oauth2/**",         // OAuth2 경로 허용
                                "/login/**"           // 로그인 경로 허용
                        ).permitAll() 
                        .anyRequest().authenticated()  // 나머지 요청들은 인증 필요
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class)  // JWT 필터 추가
                .build();
    }
}
