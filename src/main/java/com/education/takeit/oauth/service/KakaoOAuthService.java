package com.education.takeit.oauth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.oauth.client.KakaoOauthClient;
import com.education.takeit.oauth.dto.OAuthLoginRequest;
import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService implements OAuthService {

    private final KakaoOauthClient kakaoClient;
    private final UserRepository userRepository;
    private final OidcPublicKeyService oidcService;

    private final JwtUtils jwtUtils;

    /**
     * Kakao OAuth 소셜 로그인 메인 로직
     *
     * @param request
     * @return
     */
    @Override
    public Map<String, String> login(OAuthLoginRequest request) {
        /* 토큰 발급을 위한 RestClient 요청*/
        OAuthTokenResponse token = kakaoClient.getToken(request.code());

        Map<String, String> userInfo = validateIdToken(token);
        LoginType loginType = request.loginType();

        User user = userRepository.findByEmailAndLoginType(userInfo.get("email"), loginType)
                .orElseGet(() -> userRepository.save(User.builder().email(userInfo.get("email"))
                        .nickname(userInfo.get("nickname"))
                        .loginType(loginType)

                        .build()));

        return jwtUtils.generateTokens(user.getUserId());
    }

    /**
     * Kakao OAuth 소셜 로그인 검증 로직
     *
     * @param token
     * @return
     */
    @Override
    public Map<String, String> validateIdToken(OAuthTokenResponse token) {
        DecodedJWT decodedJWT = JWT.decode(token.getIdToken());
        RSAPublicKey publicKey = oidcService.getMatchingKey(decodedJWT.getKeyId(), decodedJWT.getAlgorithm());

        try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            algorithm.verify(decodedJWT);  // ✅ 서명 검증
        } catch (JWTVerificationException e) {
            throw new CustomException(StatusCode.INVALID_KAKAO_ID_TOKEN);
        }
        String email = decodedJWT.getClaim("email").asString();
        String nickname = decodedJWT.getClaim("nickname").asString();

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("email", email);
        userInfo.put("nickname", nickname);

        return userInfo;
    }
}

