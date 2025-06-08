package com.education.takeit.oauth.client;

import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.oauth.property.GoogleProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class GoogleOauthClient {

  private final RestClient restClient;
  private final GoogleProperties properties;

  @Retryable(
      value = {HttpServerErrorException.class}, // 5xx 에러 발생했을 때만 재시도
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000) // 재시도 간격
      )
  public OAuthTokenResponse getToken(String code) {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "authorization_code");
    form.add("code", code);
    form.add("client_id", properties.getClientId());
    form.add("client_secret", properties.getClientSecret());
    form.add("redirect_uri", properties.getRedirectUri());

    return restClient
        .post()
        .uri("https://oauth2.googleapis.com/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(form)
        .retrieve()
        .onStatus(
            status -> status.is4xxClientError(),
            (req, res) -> {
              log.warn("Google 토큰 요청 실패: 상태 코드={} ", res.getStatusCode());
                throw new CustomException(StatusCode.BAD_REQUEST);
            })
        .onStatus(
            status -> status.is5xxServerError(),
            (req, res) -> {
              log.error("Google 토큰 요청 실패: 상태 코드={}", res.getStatusCode());
                throw new CustomException(StatusCode.OAUTH_SERVER_ERROR);
            })
        .body(OAuthTokenResponse.class);
  }
}
