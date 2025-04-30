package com.education.takeit.oauth.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.oauth.client.KakaoOauthClient;
import com.education.takeit.oauth.dto.OAuthLoginRequest;
import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KakaoOAuthServiceTest {

  private KakaoOauthClient kakaoOauthClient;
  private UserRepository userRepository;
  private OidcPublicKeyService oidcPublicKeyService;
  private JwtUtils jwtUtils;
  private KakaoOAuthService kakaoOAuthService;

  private RSAPublicKey publicKey;
  private RSAPrivateKey privateKey;
  private RSAPublicKey wrongPublicKey;

  @BeforeEach
  void setUp() throws Exception {
    kakaoOauthClient = mock(KakaoOauthClient.class);
    userRepository = mock(UserRepository.class);
    oidcPublicKeyService = mock(OidcPublicKeyService.class);
    jwtUtils = mock(JwtUtils.class);

    kakaoOAuthService =
        new KakaoOAuthService(kakaoOauthClient, userRepository, oidcPublicKeyService, jwtUtils);

    // RSA 키쌍 생성
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(2048);
    KeyPair keyPair = generator.generateKeyPair();
    publicKey = (RSAPublicKey) keyPair.getPublic();
    privateKey = (RSAPrivateKey) keyPair.getPrivate();

    // 검증시 사용할 "틀린" public key
    KeyPair wrongKeyPair = generator.generateKeyPair();
    wrongPublicKey = (RSAPublicKey) wrongKeyPair.getPublic();
  }

  @Test
  @DisplayName("Google OAuth login() - 신규 유저 회원가입 + 토큰 발급 성공")
  void login_success() {
    // given
    String email = "test@test.com";
    String nickname = "test";

    String idToken =
        JWT.create()
            .withKeyId("mock-kid")
            .withClaim("email", email)
            .withClaim("nickname", nickname)
            .sign(Algorithm.RSA256(publicKey, privateKey));

    OAuthTokenResponse tokenResponse =
        OAuthTokenResponse.builder()
            .accessToken("mock-access-token")
            .tokenType("Bearer")
            .idToken(idToken)
            .build();

    OAuthLoginRequest loginRequest = new OAuthLoginRequest("mock-code", LoginType.KAKAO, null);

    when(kakaoOauthClient.getToken("mock-code")).thenReturn(tokenResponse);
    when(oidcPublicKeyService.getMatchingKey("mock-kid", "RS256")).thenReturn(publicKey);
    when(userRepository.findByEmailAndLoginType(email, LoginType.KAKAO))
        .thenReturn(Optional.empty());

    User savedUser =
        User.builder().email(email).nickname(nickname).loginType(LoginType.KAKAO).build();
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    when(jwtUtils.generateTokens(savedUser.getUserId()))
        .thenReturn(
            Map.of(
                "accessToken", "new-mock-access-token",
                "refreshToken", "new-mock-refresh-token"));

    // when
    Map<String, String> tokens = kakaoOAuthService.login(loginRequest);

    // then
    SoftAssertions softly = new SoftAssertions();

    softly.assertThat(tokens).containsKeys("accessToken", "refreshToken");
    softly.assertThat(tokens.get("accessToken")).isEqualTo("new-mock-access-token");
    softly.assertThat(tokens.get("refreshToken")).isEqualTo("new-mock-refresh-token");

    softly.assertAll();
  }

  @Test
  @DisplayName("Google OAuth login() - 잘못된 ID Token이 들어오면 예외가 발생한다")
  void login_fail() {
    // given
    String email = "test@test.com";
    String nickname = "test";

    String idToken =
        JWT.create()
            .withKeyId("mock-kid") // kid 설정 중요
            .withClaim("email", email)
            .withClaim("nickname", nickname)
            .sign(Algorithm.RSA256(publicKey, privateKey));

    OAuthTokenResponse tokenResponse =
        OAuthTokenResponse.builder()
            .accessToken("mock-access-token")
            .tokenType("mock-token-type")
            .idToken(idToken)
            .build();

    when(oidcPublicKeyService.getMatchingKey("mock-kid", "RS256")).thenReturn(wrongPublicKey);

    // when & then
    assertThatThrownBy(() -> kakaoOAuthService.validateIdToken(tokenResponse))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(StatusCode.INVALID_KAKAO_ID_TOKEN.getMessage());
  }
}
