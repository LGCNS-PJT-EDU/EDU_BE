package com.education.takeit.oauth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.oauth.dto.OIDCPublicKeysResponse;

@FeignClient(name = "kakaoClient", url = "https://kauth.kakao.com")
public interface KakaoOauthClient {

	@PostMapping(value = "/oauth/token", consumes = "application/x-www-form-urlencoded")
	OAuthTokenResponse getToken(
		@RequestParam("grant_type") String grantType,
		@RequestParam("client_id") String clientId,
		@RequestParam("redirect_uri") String redirectUri,
		@RequestParam("code") String code,
		@RequestParam("client_secret") String clientSecret,
		@RequestParam("scope") String scope
	);

	@GetMapping("/.well-known/jwks.json")
	OIDCPublicKeysResponse getPublicKeys();
}
