package com.education.takeit.user.service;

import com.education.takeit.user.config.NaverProperties;
import com.education.takeit.user.dto.response.NaverTokenResponse;
import com.education.takeit.user.dto.response.NaverUserResponse;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NaverLoginService implements OAuth2LoginService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final NaverProperties naverProperties;

    @Override
    public LoginType supports() {
        return LoginType.NAVER;
    }

    @Override
    public User toEntityUser(String code, LoginType loginType) {
        String accessToken = toRequestAccessToken(code);
        NaverUserResponse.NaverUserDetail profile = toRequestProfile(accessToken);

        return User.builder() // User 엔티티 객체 생성
                .email(profile.getEmail())
                .nickname(profile.getNickname())
                .loginType(LoginType.NAVER)
                .build();
    }

    // code를 AccessToken으로 교환
    private String toRequestAccessToken(String code) {
        ResponseEntity<NaverTokenResponse> response =
                restTemplate.exchange(
                        naverProperties.getTokenRequestUrl(code), // 토큰 요청 URL 생성
                        HttpMethod.GET,
                        null, // 요청 바디 필요 없음
                        NaverTokenResponse.class
                );
        return response.getBody().getAccessToken();
    }
   // AccessToken으로 네이버 사용자 정보 가져오기
    private NaverUserResponse.NaverUserDetail toRequestProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // Authorization 헤더 설정

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<NaverUserResponse> response =
                restTemplate.exchange(
                        "https://openapi.naver.com/v1/nid/me", // 사용자 정보 요청할 주소
                        HttpMethod.GET,
                        request,
                        NaverUserResponse.class);
        return response.getBody().getNaverUserDetail();
    }
}