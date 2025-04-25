package com.education.takeit.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

@Data
@Configuration
@ConfigurationProperties(prefix="naver")
public class NaverProperties {
    private String clientId;
    private String clientSecret;
    private String requestTokenUri;
    private String userInfoUri;
    private String redirectUri;
// AccessToken 받을 Url 생성
    public String getTokenRequestUrl(String code) {
        return UriComponentsBuilder.fromHttpUrl(requestTokenUri)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .queryParam("redirect_uri", redirectUri)
                .toUriString();
    }
}
