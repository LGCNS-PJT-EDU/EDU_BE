package com.education.takeit.global.config;

import com.education.takeit.global.security.JwtAuthenticationFilter;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.global.security.service.CustomUserDetailService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Value("${client.base-url}")
  private String baseUrl;

  private final String[] swaggerPath = {
    "/swagger-ui/**",
    "/v3/api-docs/**",
    "/swagger-resources/**",
    "/api-docs/**",
    "/swagger-ui.html",
    "/error"
  };

  private final JwtUtils jwtUtils;
  private final CustomUserDetailService customUserDetailService;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(jwtUtils, customUserDetailService);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
        .headers(headers -> headers.frameOptions(frame -> frame.disable())) // H2 콘솔을 사용할 경우
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(swaggerPath)
                    .permitAll() // Swagger 관련 경로 허용
                    .requestMatchers(
                        "/api/user/signin",
                        "/api/user/signup",
                        "/h2-console/**", // H2 콘솔 허용
                        "/oauth2/**",
                        "/api/oauth/**", // OAuth2 경로 허용
                        "/login/**", // 로그인 경로 허용
                        "/api/user/check-email", // 회원가입시 이메일 중복확인
                        "/api/diagnosis", // 진단 경로
                        "/api/roadmap",
                        "/api/user/refresh", // 리프레시 토큰 발급
                        "/user/**",
                        "/api/roadmap/default",
                        "/health",
                        "/actuator/health")
                    .permitAll()
                    .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated() // 나머지 요청들은 인증 필요
            )
        .addFilterBefore(
            jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    // WebConfig 설정 뿐만 아니라, SecurityConfig 안에 http.cors() 설정과 CorsCOnfigurationSource Bean 등록은 필수이다!
    // WebConfig 설정은 Spring MVC 레벨에서의 CORS 처리 담당.
    // 따라서 SpringSecurity도 따로 CORS 설정을 명시해줘야 함.
    CorsConfiguration configuration = new CorsConfiguration();
    // configuration.setAllowedOrigins(List.of(baseUrl));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedOriginPatterns(List.of(baseUrl));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setExposedHeaders(List.of("Authorization"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
