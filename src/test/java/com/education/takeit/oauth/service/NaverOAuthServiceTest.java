package com.education.takeit.oauth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.education.takeit.global.exception.CustomException;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.oauth.client.NaverOauthClient;
import com.education.takeit.oauth.dto.NaverUserResponse;
import com.education.takeit.oauth.dto.OAuthLoginRequest;
import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.user.dto.UserSigninResDto;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.Role;
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
            .refreshToken("mock-refresh-token")
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
    when(jwtUtils.generateTokens(Role.USER, mockUser.getUserId(), mockUser.getPrivacyStatus()))
        .thenReturn(new UserSigninResDto("mock-access-token", "mock-refresh-token"));

    UserSigninResDto tokens = naverOAuthService.login(loginRequest);

    // then
    assertThat(tokens.accessToken()).isEqualTo("mock-access-token");
    assertThat(tokens.refreshToken()).isEqualTo("mock-refresh-token");

    // 메서드 호출 검증
    verify(naverOauthClient).getToken("mock-code", "mock-state");
    verify(naverOauthClient).getUserInfo("mock-access-token");
    verify(userRepository).findByEmailAndLoginType(userInfo.getEmail(), LoginType.NAVER);
    verify(jwtUtils).generateTokens(Role.USER, mockUser.getUserId(), mockUser.getPrivacyStatus());
  }

  @Test
  void login_token_request_fail() { // 토큰 요청 실패 시
    OAuthLoginRequest loginRequest =
        new OAuthLoginRequest("mock-code", LoginType.NAVER, "mock-state");

    when(naverOauthClient.getToken("mock-code", "mock-state"))
        .thenThrow(new RuntimeException("네이버 토큰 요청 실패"));
    // 예외 발생 검증
    assertThatThrownBy(() -> naverOAuthService.login(loginRequest))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("네이버 토큰 요청 실패");

    verify(naverOauthClient).getToken("mock-code", "mock-state");
    // 토큰 요청 실패했을 때는 한번도 실행되면 안됨
    verify(naverOauthClient, never()).getUserInfo(any());
    verify(userRepository, never()).findByEmailAndLoginType(any(), any());
    verify(jwtUtils, never()).generateTokens(Role.USER, any(), any());
  }

  @Test
  void login_fail_when_state_missing() { // state 값 빠졌을 때
    OAuthLoginRequest loginRequest = new OAuthLoginRequest("mock-code", LoginType.NAVER, null);

    assertThatThrownBy(() -> naverOAuthService.login(loginRequest))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining("NAVER STATE 값이 비어있습니다.");

    verify(naverOauthClient, never()).getToken(any(), any());
    verify(userRepository, never()).findByEmailAndLoginType(any(), any());
    verify(jwtUtils, never()).generateTokens(Role.USER, any(), any());
  }

  @Test
  void login_fail_user_info_request_fails() { // 네이버 유저 정보 요청 실패
    OAuthLoginRequest loginRequest =
        new OAuthLoginRequest("mock-code", LoginType.NAVER, "mock-state");

    OAuthTokenResponse tokenResponse =
        OAuthTokenResponse.builder()
            .accessToken("mock-access-token")
            .tokenType("mock-token-type")
            .build();

    when(naverOauthClient.getToken("mock-code", "mock-state")).thenReturn(tokenResponse);
    when(naverOauthClient.getUserInfo("mock-access-token"))
        .thenThrow(new RuntimeException("사용자 정보 요청 실패"));

    assertThatThrownBy(() -> naverOAuthService.login(loginRequest))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("사용자 정보 요청 실패");

    verify(naverOauthClient).getToken("mock-code", "mock-state");
    verify(naverOauthClient).getUserInfo("mock-access-token");
    verify(userRepository, never()).findByEmailAndLoginType(any(), any());
    verify(jwtUtils, never()).generateTokens(Role.USER, any(), any());
  }
}
