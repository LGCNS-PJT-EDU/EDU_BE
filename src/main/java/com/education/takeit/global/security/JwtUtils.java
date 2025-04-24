package com.education.takeit.global.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtils {
	private String secretKey = "AbcDefGhijkLmnOpQRStuvwXYZ1234567890!@#"; // 임시

	private long expiration = 86400000; // 임시입니다.

	private Key key;

	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public String createToken(Long userId, String email) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expiration);

		return Jwts.builder()
			.setSubject(userId.toString())
			.claim("email", email)
			.setIssuedAt(now)
			.setExpiration(expiry)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public Long getUserId(String token) {
		return Long.parseLong(Jwts.parser()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject());
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	// 이메일 기반 회원 정보 처리를 위해 추가한 메서드
	public String getEmail(String token) {
		return Jwts.parser()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.get("email", String.class);
	}
}
