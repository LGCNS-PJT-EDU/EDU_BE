package com.education.takeit.oauth.service;

import java.security.interfaces.RSAPublicKey;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.global.exception.StatusCode;
import com.education.takeit.oauth.dto.KakaoLoginRequest;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
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

	private final UserRepository userRepository;
	private final KakaoProperties properties;
	private final JwtUtils jwtUtils;

	public String loginWithKakao(KakaoLoginRequest request) {
		OAuthTokenResponse tokenResponse = kakaoClient.getToken(
			"authorization_code",
			properties.getClientId(),
			properties.getRedirectUri(),
			request.getCode(),
			properties.getClientSecret(),
			properties.getScope()
		);
		DecodedJWT decodedJWT = JWT.decode(tokenResponse.getIdToken());
		RSAPublicKey publicKey = oidcService.getMatchingKey(decodedJWT.getKeyId(), decodedJWT.getAlgorithm());

		try {
			Algorithm algorithm = Algorithm.RSA256(publicKey, null);
			algorithm.verify(decodedJWT);  // ✅ 서명 검증
		} catch (JWTVerificationException e) {
			throw new CustomException(StatusCode.INVALID_KAKAO_ID_TOKEN); // ❌ 서명 검증 실패 시 예외 처리
		}

		String nickname = decodedJWT.getClaim("nickname").asString();
		String email = decodedJWT.getClaim("email").asString();
		LoginType loginType = request.getLoginType();

		User user = userRepository.findByEmailAndLoginType(email, loginType)
				.orElseGet(() -> userRepository.save(User.builder()
						.email(email)
						.nickname(nickname)
						.loginType(loginType)
						.build()));

		return jwtUtils.createToken(user.getUserId(), user.getEmail());
	}
}

