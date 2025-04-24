package com.education.takeit.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.education.takeit.global.security.JwtAuthenticationFilter;
import com.education.takeit.global.security.JwtUtils;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtUtils jwtUtils;

	private final String[] swaggerPath = {"/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/api-docs/**",
		"/swagger-ui.html", "/error"};

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(csrf -> csrf.disable())
			.headers(headers -> headers.frameOptions(frame -> frame.disable())) // H2 콘솔용
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(swaggerPath).permitAll() // 스웨거 경로 설정
				.requestMatchers(
					"/api/user/signin",     // 로그인 허용
					"/api/user/signup",     // 회원가입 허용
					"/h2-console/**"      // H2 콘솔 허용 (개발 시)
				).permitAll()
				.anyRequest().authenticated() // 나머지 요청은 인증 필요
			)
			.addFilterBefore(new JwtAuthenticationFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class)
			.build();
	}

}
