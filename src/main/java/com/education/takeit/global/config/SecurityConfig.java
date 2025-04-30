package com.education.takeit.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.education.takeit.global.security.JwtAuthenticationFilter;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.global.security.service.CustomUserDetailService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final String[] swaggerPath = {"/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/api-docs/**",
		"/swagger-ui.html", "/error"};

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
		return http
			.csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
			.headers(headers -> headers.frameOptions(frame -> frame.disable()))  // H2 콘솔을 사용할 경우
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(swaggerPath).permitAll()  // Swagger 관련 경로 허용
				.requestMatchers(
					"/api/user/signin",
					"/api/user/signup",
					"/h2-console/**",     // H2 콘솔 허용
					"/oauth2/**",
					"/api/user/oauth/naver",// OAuth2 경로 허용
					"/login/**",          // 로그인 경로 허용
					"/api/user/check-email",
						"/api/user/reissue",// 회원가입시 이메일 중복확인
					"/api/auth/**"        //임시 허용
				).permitAll()
				.anyRequest().authenticated()  // 나머지 요청들은 인증 필요
			)
			.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)  // JWT 필터 추가
			.build();
	}
}