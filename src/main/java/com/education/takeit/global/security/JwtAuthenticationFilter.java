package com.education.takeit.global.security;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.security.service.CustomUserDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtils jwtUtils;
  private final CustomUserDetailService customUserDetailService;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  private final List<String> EXCLUDE_PATHS =
      List.of(
          "/api/oauth/**",
          "/api/user/signin",
          "/api/user/signup",
          "/api/user/check-email",
          "/api/user/refresh",
          "/error",
          "/swagger-ui/**",
          "/swagger-ui.html",
          "/v3/api-docs/**",
          "/swagger-resources/**");

  public JwtAuthenticationFilter(
      JwtUtils jwtUtils, CustomUserDetailService customUserDetailService) {
    this.jwtUtils = jwtUtils;
    this.customUserDetailService = customUserDetailService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String token = resolveToken(request);

      // 1. 토큰이 없으면 다음 필터로 넘기되, 인증은 하지 않음
      if (token == null) {
        filterChain.doFilter(request, response);
        return;
      }

      // 2. 토큰이 있지만 유효하지 않으면 예외 발생 → 직접 401 응답
      if (!jwtUtils.validateToken(token)) {
        setErrorResponse(response, StatusCode.INVALID_TOKEN);
        return;
      }

      // 3. 유효한 토큰이라면 인증 수행
      Long userId = jwtUtils.getUserId(token);
      UserDetails userDetails = customUserDetailService.loadUserByUsername(userId.toString());

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);

      filterChain.doFilter(request, response);

    } catch (ExpiredJwtException e) {
      setErrorResponse(response, StatusCode.UNAUTHORIZED); // 401
    } catch (JwtException | IllegalArgumentException e) {
      setErrorResponse(response, StatusCode.INVALID_TOKEN); // 401
    }
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String uri = request.getRequestURI();
    return EXCLUDE_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  private void setErrorResponse(HttpServletResponse response, StatusCode status) {
    response.setStatus(status.getStatusCode());
    response.setContentType("application/json;charset=UTF-8");
    try {
      Message errorMessage =
          new Message(StatusCode.INVALID_TOKEN, objectMapper.writeValueAsString(status));
      String json = objectMapper.writeValueAsString(errorMessage);
      response.getWriter().write(json);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
