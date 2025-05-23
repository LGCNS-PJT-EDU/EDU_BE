package com.education.takeit.oauth.client;

import com.education.takeit.oauth.dto.NaverUserResponse;
import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.oauth.property.NaverProperties;
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
public class NaverOauthClient {

  private final RestClient restClient;
  private final NaverProperties properties;

  @Retryable(
      value = {HttpServerErrorException.class}, // 5xx 에러 발생했을 때만 재시도
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000) // 재시도 간격
      )
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
        .onStatus(
            status -> status.is4xxClientError(),
            (req, res) -> {
              log.warn("Naver 토큰 요청 실패: 상태코드={} ", res.getStatusCode());
              throw new BadRequestException("잘못된 요청입니다");
            })
        .onStatus(
            status -> status.is5xxServerError(),
            (req, res) -> {
              log.error("Naver 토큰 요청 실패: 상태코드={} ", res.getStatusCode());
              throw new HttpServerErrorException(res.getStatusCode());
            })
        .body(OAuthTokenResponse.class);
  }

  public NaverUserResponse getUserInfo(String accessToken) {
    return restClient
        .get()
        .uri(properties.getUserInfoUri())
        .headers(headers -> headers.setBearerAuth(accessToken))
        .retrieve()
        .onStatus(
            status -> status.is4xxClientError(),
            (req, res) -> {
              log.warn("Naver 사용자 정보 요청 실패: 상태 코드={}", res.getStatusCode());
              throw new BadRequestException(" 잘못된 요청입니다.");
            })
        .onStatus(
            status -> status.is5xxServerError(),
            (req, res) -> {
              log.error("Naver 사용자 정보 요청 실패: 상태 코드={}", res.getStatusCode());
              throw new HttpServerErrorException(res.getStatusCode());
            })
        .body(NaverUserResponse.class);
  }
}
