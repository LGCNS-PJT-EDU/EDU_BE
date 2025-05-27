package com.education.takeit.global.security;

import com.education.takeit.user.dto.UserSigninResDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtils {
  private final RedisTemplate<String, String> redisTemplate;

  private String secretKey = "AbcDefGhijkLmnOpQRStuvwXYZ1234567890!@#"; // 임시
  private final long accessTokenExpiration = 1000L * 60 * 15; // 액세스 토큰 유효시간 : 15분
  private final long refreshTokenExpiration = 1000L * 60 * 60 * 24 * 7; // 리프레시 토큰 유효시간 : 7일

  private Key key;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  // 액세스, 리프레시 토큰 함께 생성
  public UserSigninResDto generateTokens(Long userId) {
    Date now = new Date();

    // 액세스 토큰
    Date accessExpiry = new Date(now.getTime() + accessTokenExpiration);
    String accessToken =
        Jwts.builder()
            .setSubject(userId.toString())
            .claim("userId", userId)
            .setIssuedAt(now)
            .setExpiration(accessExpiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

    // 리프레시 토큰
    Date refreshExpiry = new Date(now.getTime() + refreshTokenExpiration);
    String refreshToken =
        Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(now)
            .setExpiration(refreshExpiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

    // Redis에 저장 (key: {userId}'s refresh token, value: refreshToken)
    redisTemplate
        .opsForValue()
        .set(
            userId + "'s refresh token",
            refreshToken,
            refreshTokenExpiration,
            TimeUnit.MILLISECONDS);

    return new UserSigninResDto(accessToken, refreshToken);
  }

  //  엑세스 토큰 검증
  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  // 리프레시 토큰 검증 후 엑세스 토큰 재발급
  public String generateAccessToken(Long userId) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + accessTokenExpiration);

    return Jwts.builder()
        .setSubject(userId.toString())
        .claim("userId", userId)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Long getUserId(String token) {
    try {
      Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();

      return Long.parseLong(claims.getSubject());
    } catch (ExpiredJwtException e) { // 토큰이 만료되었을 경우 claims만 꺼내기
      return Long.parseLong(e.getClaims().getSubject());
    }
  }

  // Redis에 저장된 리프레시 토큰 검증
  public boolean validateRefreshToken(Long userId, String refreshToken) {
    String storedRefreshToken = redisTemplate.opsForValue().get(userId + "'s refresh token");

    return storedRefreshToken != null && storedRefreshToken.equals(refreshToken);
  }

  public String resolveToken(String bearerToken) {
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return bearerToken;
  }
}
