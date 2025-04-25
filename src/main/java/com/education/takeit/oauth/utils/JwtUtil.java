//package com.education.takeit.oauth.utils;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//
//public class JwtUtil {
//	private static final String SECRET = "MySuperSecureJwtSecretKeyWithEnoughLength123!";
//	private static final long EXPIRATION = 86400000;
//
//	public static String createJwt(String userId, String email) {
//		return Jwts.builder()
//			.setSubject(userId)
//			.claim("email", email)
//			.setIssuedAt(new Date())
//			.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
//			.signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
//			.compact();
//	}
//}
//
