package com.education.takeit.oauth.service;

import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.oauth.client.NaverOauthClient;
import com.education.takeit.oauth.dto.NaverUserResponse;
import com.education.takeit.oauth.dto.OAuthLoginRequest;
import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

public class NaverOAuthServiceTest {
    private NaverOauthClient naverOauthClient;
    private UserRepository userRepository;
    private JwtUtils jwtUtils;
    private NaverOAuthService naverOAuthService;

    @BeforeEach
    void setUp() throws Exception{
        naverOauthClient = mock(NaverOauthClient.class);
        userRepository = mock(UserRepository.class);
        jwtUtils = mock(JwtUtils.class);

        naverOAuthService = new NaverOAuthService(
                naverOauthClient,
                userRepository,
                jwtUtils);
    }

    @Test
    void login_success(){

        OAuthLoginRequest loginRequest = new OAuthLoginRequest(
                "mock-code",
                LoginType.NAVER,
                "mock-state"
        );
        OAuthTokenResponse tokenResponse = OAuthTokenResponse.builder()
                .accessToken("mock-access-token")
                .tokenType("mock-token-type")
                .build();

        NaverUserResponse.NaverUserInfo userInfo = new NaverUserResponse.NaverUserInfo(
                "naver-id-123", // 네이버가 발급한 유저 고유 식별자
                "mock-nickname",
                "mock@email.com"
        );

        NaverUserResponse userResponse = new NaverUserResponse();
        userResponse.setNaverUserInfo(userInfo);

        User mockUser = User.builder()
                .email(userInfo.getEmail())
                .nickname(userInfo.getNickname())
                .loginType(LoginType.KAKAO)
                .build();
        Map<String,String> tokenMap = Map.of(
                "accessToken","jwt-access-token",
                "refreshToken", "jwt-refresh-token"
                );

        // when
        when(naverOauthClient.getToken("mock-code", "mock-state")).thenReturn(tokenResponse);
        when(naverOauthClient.getUserInfo("mock-access-token")).thenReturn(userResponse);
        when(userRepository.findByEmailAndLoginType(userInfo.getEmail(), LoginType.NAVER))
                .thenReturn(Optional.of(mockUser));
        when(jwtUtils.generateTokens(mockUser.getUserId())).thenReturn(tokenMap);

        // then
        Map<String, String> result = naverOAuthService.login(loginRequest);

        assertThat(result).containsEntry("accessToken", "jwt-access-token");
        assertThat(result).containsEntry("refreshToken", "jwt-refresh-token");

        verify(naverOauthClient).getToken("mock-code", "mock-state");
        verify(naverOauthClient).getUserInfo("mock-access-token");
        verify(userRepository).findByEmailAndLoginType(userInfo.getEmail(), LoginType.NAVER);
        verify(jwtUtils).generateTokens(mockUser.getUserId());
    }
}
