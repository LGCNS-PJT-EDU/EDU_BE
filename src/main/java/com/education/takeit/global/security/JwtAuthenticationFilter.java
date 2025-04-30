package com.education.takeit.global.security;

import java.io.IOException;
import java.util.List;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.education.takeit.global.security.service.CustomUserDetailService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtils jwtUtils;
	private final CustomUserDetailService customUserDetailService;

	private final List<String> EXCLUDE_PATHS = List.of(
		"/api/auth/**", "/swagger-ui", "/swagger-ui.html", "/v3/api-docs", "/swagger-resources", "/api/user/signin",
		"/api/user/signup", "/error", "/api/user/check-email","/api/user/reissue"
	);

	public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailService customUserDetailService) {
		this.jwtUtils = jwtUtils;
		this.customUserDetailService = customUserDetailService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {
		String token = resolveToken(request);

		if (token != null) {
			try {
				if (jwtUtils.validateToken(token)) {
					Long userId = jwtUtils.getUserId(token);
					UserDetails userDetails = customUserDetailService.loadUserByUsername(userId.toString());

					UsernamePasswordAuthenticationToken authentication =
							new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			} catch (ExpiredJwtException ex) {
				String uri = request.getRequestURI();
				if (!uri.equals("/api/user/reissue")) {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType("application/json");
					response.getWriter().write("{\"message\": \"엑세스 토큰 만료됨\"}");
					return;
				}
			}
		}

		filterChain.doFilter(request, response);
	}


	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String uri = request.getRequestURI();
		return EXCLUDE_PATHS.stream().anyMatch(uri::startsWith);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

}