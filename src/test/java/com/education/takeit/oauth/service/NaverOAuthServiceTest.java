package com.education.takeit.oauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.oauth.client.NaverOauthClient;
import com.education.takeit.oauth.dto.NaverUserResponse;
import com.education.takeit.oauth.dto.OAuthLoginRequest;
import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NaverOAuthServiceTest {
  private NaverOauthClient naverOauthClient;
  private UserRepository userRepository;
  private JwtUtils jwtUtils;
  private NaverOAuthService naverOAuthService;

  @BeforeEach
  void setUp() throws Exception {
    naverOauthClient = mock(NaverOauthClient.class);
    userRepository = mock(UserRepository.class);
    jwtUtils = mock(JwtUtils.class);

    naverOAuthService = new NaverOAuthService(naverOauthClient, userRepository, jwtUtils);
  }

  @Test
  void login_success() {
    // given 테스트를 위한 객체 맻 mock 데이터 설정
    OAuthLoginRequest loginRequest =
        new OAuthLoginRequest("mock-code", LoginType.NAVER, "mock-state");
    OAuthTokenResponse tokenResponse =
        OAuthTokenResponse.builder()
            .accessToken("mock-access-token")
            .tokenType("mock-token-type")
            .build();

    NaverUserResponse.NaverUserInfo userInfo =
        new NaverUserResponse.NaverUserInfo(
            "naver-id-123", // 네이버가 발급한 유저 고유 식별자
            "mock-nickname",
            "mock@email.com");

    NaverUserResponse userResponse = NaverUserResponse.builder().naverUserInfo(userInfo).build();

    User mockUser =
        User.builder()
            .email(userInfo.getEmail())
            .nickname(userInfo.getNickname())
            .loginType(LoginType.NAVER)
            .build();

    // when (login 메소드가 실행되기전에 mock 객체 동작을 설정)
    when(naverOauthClient.getToken("mock-code", "mock-state")).thenReturn(tokenResponse);
    when(naverOauthClient.getUserInfo("mock-access-token")).thenReturn(userResponse);
    when(userRepository.findByEmailAndLoginType(userInfo.getEmail(), LoginType.NAVER))
        .thenReturn(Optional.of(mockUser));
    when(jwtUtils.generateTokens(mockUser.getUserId())).thenReturn("mock-access-token");

    String tokens = naverOAuthService.login(loginRequest);

    // then
    assertThat(tokens).isEqualTo("mock-access-token");

    // 메서드 호출 검증
    verify(naverOauthClient).getToken("mock-code", "mock-state");
    verify(naverOauthClient).getUserInfo("mock-access-token");
    verify(userRepository).findByEmailAndLoginType(userInfo.getEmail(), LoginType.NAVER);
    verify(jwtUtils).generateTokens(mockUser.getUserId());
  }
}
