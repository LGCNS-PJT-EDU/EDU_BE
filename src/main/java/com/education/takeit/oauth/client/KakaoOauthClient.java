package com.education.takeit.oauth.client;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.oauth.dto.OIDCPublicKeysResponse;
import com.education.takeit.oauth.property.KakaoProperties;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoOauthClient {

	private final RestClient restClient;
	private final KakaoProperties properties;

	public OAuthTokenResponse getToken(String code) {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("grant_type", "authorization_code");
		form.add("client_id", properties.getClientId());
		form.add("redirect_uri", properties.getRedirectUri());
		form.add("code", code);
		form.add("client_secret", properties.getClientSecret());
		form.add("scope", properties.getScope());

		return restClient.post()
			.uri("https://kauth.kakao.com/oauth/token")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(form)
			.retrieve()
			.body(OAuthTokenResponse.class);
	}

	public OIDCPublicKeysResponse getPublicKeys() {
		return restClient.get()
			.uri("https://kauth.kakao.com/.well-known/jwks.json")
			.retrieve()
			.body(OIDCPublicKeysResponse.class);
	}
}
