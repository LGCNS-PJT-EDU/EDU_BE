package com.education.takeit.oauth.service;

import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.oauth.client.NaverOauthClient;
import com.education.takeit.oauth.dto.NaverUserResponse;
import com.education.takeit.oauth.dto.OAuthLoginRequest;
import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.user.dto.UserSigninResDto;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NaverOAuthService implements OAuthService {
  private final NaverOauthClient naverClient;
  private final UserRepository userRepository;
  private final JwtUtils jwtUtils;

  @Override
  public UserSigninResDto login(OAuthLoginRequest request) {
    if (request.state() == null || request.state().isBlank()) {
      throw new CustomException(StatusCode.MISSING_NAVER_STATE);
    }

    OAuthTokenResponse token = naverClient.getToken(request.code(), request.state());
    NaverUserResponse userResponse = naverClient.getUserInfo(token.getAccessToken());
    NaverUserResponse.NaverUserInfo userInfo = userResponse.getNaverUserInfo();

    User user =
        userRepository
            .findByEmailAndLoginType(userInfo.getEmail(), LoginType.NAVER)
            .orElseGet(
                () ->
                    userRepository.save(
                        User.builder()
                            .email(userInfo.getEmail())
                            .nickname(userInfo.getNickname())
                            .loginType(LoginType.NAVER)
                            .build()));
    return jwtUtils.generateTokens(user.getUserId());
  }

  @Override
  public Map<String, String> validateIdToken(OAuthTokenResponse tokenResponse) {
    throw new UnsupportedOperationException("Naver 로그인은 ID Token을 사용하지 않습니다.");
  }
}
