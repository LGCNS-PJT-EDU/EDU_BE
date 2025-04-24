package com.education.takeit.oauth.service;

import java.security.interfaces.RSAPublicKey;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.oauth.client.KakaoOauthClient;
import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.oauth.property.KakaoProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

	private final KakaoOauthClient kakaoClient;
	private final OidcPublicKeyService oidcService;
	private final KakaoProperties properties;
	private final JwtUtils jwtUtils;

	public String loginWithKakao(String code) {
		OAuthTokenResponse tokenResponse = kakaoClient.getToken(
			"authorization_code",
			properties.getClientId(),
			properties.getRedirectUri(),
			code,
			properties.getClientSecret()
		);
		DecodedJWT decodedJWT = JWT.decode(tokenResponse.getIdToken());

		RSAPublicKey publicKey = oidcService.getMatchingKey(decodedJWT.getKeyId(), decodedJWT.getAlgorithm());

		Algorithm algorithm = Algorithm.RSA256(publicKey, null);
		algorithm.verify(decodedJWT);  // ✅ 서명 검증

		String userId = decodedJWT.getSubject();
		String email = decodedJWT.getClaim("email").asString();

		// 여기서 DB에 사용자 등록 or 조회 로직 수행
		return jwtUtils.createToken(1234L, email);
	}
}

