package com.education.takeit.oauth.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.oauth.client.GoogleOauthClient;
import com.education.takeit.oauth.dto.OAuthLoginRequest;
import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.user.dto.UserSigninResDto;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.Role;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

class GoogleOAuthServiceTest {
  private GoogleOauthClient googleOauthClient;
  private UserRepository userRepository;
  private GoogleIdTokenVerifier googleIdTokenVerifier;
  private JwtUtils jwtUtils;
  private GoogleOAuthService googleOAuthService;

  @BeforeEach
  void setUp() {
    googleOauthClient = mock(GoogleOauthClient.class);
    userRepository = mock(UserRepository.class);
    googleIdTokenVerifier = mock(GoogleIdTokenVerifier.class);
    jwtUtils = mock(JwtUtils.class);

    googleOAuthService =
        new GoogleOAuthService(googleOauthClient, userRepository, googleIdTokenVerifier, jwtUtils);
  }

  @Test
  @DisplayName("Google OAuth login() - 신규 유저 회원가입 + 토큰 발급 성공")
  void login_success() throws GeneralSecurityException, IOException {
    // given
    String email = "test@test.com";
    String nickname = "test";

    OAuthTokenResponse tokenResponse =
        OAuthTokenResponse.builder()
            .accessToken("mock-access-token")
            .refreshToken("mock-refresh-token")
            .tokenType("Bearer")
            .idToken("mock-id-token")
            .build();

    OAuthLoginRequest loginRequest = new OAuthLoginRequest("mock-code", LoginType.GOOGLE, null);

    GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);
    when(payload.getEmail()).thenReturn(email);
    when(payload.get("name")).thenReturn(nickname);

    GoogleIdToken idToken = mock(GoogleIdToken.class);
    when(idToken.getPayload()).thenReturn(payload);

    when(googleOauthClient.getToken("mock-code")).thenReturn(tokenResponse);
    when(googleIdTokenVerifier.verify("mock-id-token")).thenReturn(idToken);
    when(userRepository.findByEmailAndLoginType(email, LoginType.GOOGLE))
        .thenReturn(Optional.empty());

    User savedUser =
        User.builder()
            .email(email)
            .nickname(nickname)
            .loginType(LoginType.GOOGLE)
            .role(Role.USER)
            .build();

    when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(savedUser);

    when(jwtUtils.generateTokens(
            savedUser.getRole(), savedUser.getUserId(), savedUser.getPrivacyStatus()))
        .thenReturn(new UserSigninResDto("new-mock-access-token", "new-mock-refresh-token", true));

    // when
    UserSigninResDto tokens = googleOAuthService.login(loginRequest);

    // then
    SoftAssertions softly = new SoftAssertions();

    softly.assertThat(tokens.accessToken()).isEqualTo("new-mock-access-token");
    softly.assertThat(tokens.refreshToken()).isEqualTo("new-mock-refresh-token");
    softly.assertAll();
  }

  @Test
  @DisplayName("Google OAuth login() -잘못된 ID Token이 들어오면 예외가 발생한다")
  void login_fail() throws GeneralSecurityException, IOException {
    // given
    OAuthTokenResponse tokenResponse =
        OAuthTokenResponse.builder()
            .accessToken("mock-access-token")
            .refreshToken("mock-refresh-token")
            .tokenType("Bearer")
            .idToken("invalid-id-token")
            .build();

    OAuthLoginRequest loginRequest = new OAuthLoginRequest("mock-code", LoginType.GOOGLE, null);

    when(googleOauthClient.getToken("mock-code")).thenReturn(tokenResponse);
    when(googleIdTokenVerifier.verify("invalid-id-token")).thenReturn(null);

    // when & then
    assertThatThrownBy(() -> googleOAuthService.login(loginRequest))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(StatusCode.INVALID_GOOGLE_ID_TOKEN.getMessage());
  }
}
