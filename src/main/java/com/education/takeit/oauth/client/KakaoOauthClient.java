package com.education.takeit.oauth.client;

import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.oauth.dto.OIDCPublicKeysResponse;
import com.education.takeit.oauth.property.KakaoProperties;
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
public class KakaoOauthClient {

  private final RestClient restClient;
  private final KakaoProperties properties;

  @Retryable(
          value = {HttpServerErrorException.class}, // 5xx 에러 발생했을 때만 재시도
          maxAttempts = 3,
          backoff = @Backoff(delay = 1000) // 재시도 간격
  )

  public OAuthTokenResponse getToken(String code) {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "authorization_code");
    form.add("client_id", properties.getClientId());
    form.add("redirect_uri", properties.getRedirectUri());
    form.add("code", code);
    form.add("client_secret", properties.getClientSecret());
    form.add("scope", properties.getScope());

    return restClient
        .post()
        .uri("https://kauth.kakao.com/oauth/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(form)
        .retrieve()
            .onStatus(status-> status.is4xxClientError(),(req,res)-> {
              log.warn("카카오 토큰 요청 실패: 상태 코드", res.getStatusCode());
              throw new BadRequestException("잘못된 요청입니다.");
            })
            .onStatus(status-> status.is5xxServerError(),(req,res)-> {
              log.error("카카오 토큰 요청 실패: 상태 코드", res.getStatusCode());
              throw new HttpServerErrorException(res.getStatusCode());
            })
        .body(OAuthTokenResponse.class);
  }

  public OIDCPublicKeysResponse getPublicKeys() {
    return restClient
        .get()
        .uri("https://kauth.kakao.com/.well-known/jwks.json")
        .retrieve()
            .onStatus(status-> status.is4xxClientError(),(req,res)-> {
              log.warn("카카오 OIDC 키 발급 요청 실패: 상태 코드", res.getStatusCode());
              throw new BadRequestException("잘못된 요청입니다.");
            })
            .onStatus(status-> status.is5xxServerError(),(req,res)-> {
              log.error("카카오 OIDC 키 발급 요청 실패: 상태 코드", res.getStatusCode());
              throw new HttpServerErrorException(res.getStatusCode());
            })
        .body(OIDCPublicKeysResponse.class);
  }
}
