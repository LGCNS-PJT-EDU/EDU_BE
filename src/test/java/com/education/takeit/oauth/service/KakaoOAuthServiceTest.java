package com.education.takeit.oauth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.oauth.client.KakaoOauthClient;
import com.education.takeit.oauth.dto.OAuthLoginRequest;
import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class KakaoOAuthServiceTest {

    private KakaoOauthClient kakaoOauthClient;
    private UserRepository userRepository;
    private OidcPublicKeyService oidcPublicKeyService;
    private JwtUtils jwtUtils;
    private KakaoOAuthService kakaoOAuthService;

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    @BeforeEach
    void setUp() throws Exception {
        kakaoOauthClient = mock(KakaoOauthClient.class);
        userRepository = mock(UserRepository.class);
        oidcPublicKeyService = mock(OidcPublicKeyService.class);
        jwtUtils = mock(JwtUtils.class);

        kakaoOAuthService = new KakaoOAuthService(
                kakaoOauthClient,
                userRepository,
                oidcPublicKeyService,
                jwtUtils
        );

        // RSA 키쌍 생성
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        publicKey = (RSAPublicKey) keyPair.getPublic();
        privateKey = (RSAPrivateKey) keyPair.getPrivate();
    }

    @Test
    @DisplayName("login() - 신규 유저가 없을 때 회원가입 + 토큰 발급된다")
    void login_success() {
        // given
        String email = "testuser@example.com";
        String nickname = "TestUser";

        String idToken = JWT.create()
                .withKeyId("mock-kid")
                .withClaim("email", email)
                .withClaim("nickname", nickname)
                .sign(Algorithm.RSA256(publicKey, privateKey));

        OAuthTokenResponse tokenResponse = OAuthTokenResponse.builder()
                .accessToken("mock-access-token")
                .tokenType("mock-token-type")
                .idToken(idToken)  // ✅ 여기 반드시 idToken 넣어줘야 한다
                .build();

        OAuthLoginRequest loginRequest = new OAuthLoginRequest(
                "mock-code",
                LoginType.KAKAO,
                null
        );

        when(kakaoOauthClient.getToken("mock-code")).thenReturn(tokenResponse);
        when(oidcPublicKeyService.getMatchingKey("mock-kid", "RS256")).thenReturn(publicKey);
        when(userRepository.findByEmailAndLoginType(email, LoginType.KAKAO)).thenReturn(Optional.empty());

        User savedUser = User.builder()
                .email(email)
                .nickname(nickname)
                .loginType(LoginType.KAKAO)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(jwtUtils.generateTokens(savedUser.getUserId())).thenReturn(Map.of(
                "accessToken", "new-mock-access-token",
                "refreshToken", "new-mock-refresh-token"
        ));

        // when
        Map<String, String> tokens = kakaoOAuthService.login(loginRequest);

        // then
        assertThat(tokens).containsKeys("accessToken", "refreshToken");
        assertThat(tokens.get("accessToken")).isEqualTo("new-mock-access-token");
        assertThat(tokens.get("refreshToken")).isEqualTo("new-mock-refresh-token");

        verify(kakaoOauthClient).getToken("mock-code");
        verify(userRepository).findByEmailAndLoginType(email, LoginType.KAKAO);
        verify(userRepository).save(any(User.class));
        verify(jwtUtils).generateTokens(savedUser.getUserId());
    }
}
