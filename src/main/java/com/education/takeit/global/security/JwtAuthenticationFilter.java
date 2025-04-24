package com.education.takeit.global.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtils jwtUtils;

	private final List<String> EXCLUDE_PATHS = List.of(
		"/swagger-ui", "/swagger-ui.html", "/v3/api-docs", "/swagger-resources", "/api/user/signin", "/api/user/signup",
		"/error"
	);

	public JwtAuthenticationFilter(JwtUtils jwtUtils) {
		this.jwtUtils = jwtUtils;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// Authorization 헤더 처리가 가능하도록 함
		String header = request.getHeader("Authorization");

		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);

			if (jwtUtils.validateToken(token)) {
				String email = jwtUtils.getEmail(token);

				JwtUserDetails userDetails = new JwtUserDetails(email, List.of(), true);
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
					userDetails.getAuthorities());

				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String uri = request.getRequestURI();
		return EXCLUDE_PATHS.stream().anyMatch(uri::startsWith);
	}
}
