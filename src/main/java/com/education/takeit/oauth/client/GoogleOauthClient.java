package com.education.takeit.oauth.client;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.education.takeit.oauth.dto.OAuthLoginRequest;
import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.oauth.property.GoogleProperties;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class GoogleOauthClient {

	private final RestClient restClient;
	private final GoogleProperties properties;

	public OAuthTokenResponse getToken(OAuthLoginRequest request) {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("grant_type", "authorization_code");
		form.add("code", request.getCode());
		form.add("client_id", properties.getClientId());
		form.add("client_secret", properties.getClientSecret());
		form.add("redirect_uri", properties.getRedirectUri());

		return restClient.post()
			.uri("https://oauth2.googleapis.com/token")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(form)
			.retrieve()
			.body(OAuthTokenResponse.class);
	}
}
