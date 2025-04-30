package com.education.takeit.global.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
  public Map<String, String> generateTokens(Long userId) {
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

    // 반환
    Map<String, String> tokens = new HashMap<>();
    tokens.put("accessToken", accessToken);
    tokens.put("refreshToken", refreshToken);
    return tokens;
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
    return Long.parseLong(
        Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject());
  }

  // Redis에 저장된 리프레시 토큰 검증
  public boolean validateRefreshToken(Long userId, String refreshToken) {
    String stored = redisTemplate.opsForValue().get("refresh:" + userId);
    return stored != null && stored.equals(refreshToken);
  }
}
