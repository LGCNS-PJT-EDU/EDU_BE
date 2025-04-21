package com.education.takeit.oauth.service;

import java.security.interfaces.RSAPublicKey;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.education.takeit.oauth.client.KakaoOauthClient;
import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.oauth.dto.OIDCPublicKey;
import com.education.takeit.oauth.dto.OIDCPublicKeysResponse;
import com.education.takeit.oauth.property.KakaoProperties;
import com.education.takeit.oauth.utils.JwtUtil;
import com.education.takeit.oauth.utils.PublicKeyUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

	private final KakaoOauthClient kakaoClient;
	private final KakaoProperties properties;

	public String loginWithKakao(String code) {
		OAuthTokenResponse tokenResponse = kakaoClient.getToken(
			"authorization_code",
			properties.getClientId(),
			properties.getRedirectUri(),
			code,
			properties.getClientSecret()
		);

		String idToken = tokenResponse.getIdToken();
		DecodedJWT jwt = JWT.decode(idToken);
		String kid = jwt.getKeyId();
		String alg = jwt.getAlgorithm();

		OIDCPublicKeysResponse keys = kakaoClient.getPublicKeys();
		OIDCPublicKey publicKey = keys.getMatchedKey(kid, alg);
		RSAPublicKey rsaKey = PublicKeyUtil.createRSAPublicKey(publicKey.getN(), publicKey.getE());

		Algorithm algorithm = Algorithm.RSA256(rsaKey, null);
		algorithm.verify(jwt); // 검증

		String kakaoUserId = jwt.getSubject();
		String email = jwt.getClaim("email").asString();

		return JwtUtil.createJwt(kakaoUserId, email);
	}
}

