package com.education.takeit.oauth.client;

import com.education.takeit.oauth.dto.NaverUserResponse;
import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.oauth.property.NaverProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class NaverOauthClient {

  private final RestClient restClient;
  private final NaverProperties properties;

  public OAuthTokenResponse getToken(String code, String state) {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "authorization_code");
    form.add("client_id", properties.getClientId());
    form.add("client_secret", properties.getClientSecret());
    form.add("code", code);
    form.add("state", state);

    return restClient
        .post()
        .uri(properties.getRequestTokenUri())
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(form)
        .retrieve()
        .body(OAuthTokenResponse.class);
  }

  public NaverUserResponse getUserInfo(String accessToken) {
    return restClient
        .get()
        .uri(properties.getUserInfoUri())
        .headers(headers -> headers.setBearerAuth(accessToken))
        .retrieve()
        .body(NaverUserResponse.class);
  }
}
