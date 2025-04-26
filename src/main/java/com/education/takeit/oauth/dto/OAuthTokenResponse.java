package com.education.takeit.oauth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OAuthTokenResponse {
	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("id_token")
	private String idToken;

	@JsonProperty("token_type")
	private String tokenType;
}
