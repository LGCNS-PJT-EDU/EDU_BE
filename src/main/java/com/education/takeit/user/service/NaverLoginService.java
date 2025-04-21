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

        return User.builder()
                .userId("naver_" + profile.getId())
                .email(profile.getEmail())
                .nickname(profile.getNickname())
                .password("소셜로그인패스워드없음")
                .loginType(LoginType.NAVER)
                .build();
    }
    private String toRequestAccessToken(String code) {
        ResponseEntity<NaverTokenResponse> response =
                restTemplate.exchange(naverProperties.getTokenRequestUrl(code), HttpMethod.GET, null, NaverTokenResponse.class);
        return response.getBody().getAccessToken();
    }
    private NaverUserResponse.NaverUserDetail toRequestProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<NaverUserResponse> response =
                restTemplate.exchange("https://openapi.naver.com/v1/nid/me", HttpMethod.GET, request, NaverUserResponse.class);
        return response.getBody().getNaverUserDetail();
    }
}