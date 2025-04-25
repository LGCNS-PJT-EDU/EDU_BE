package com.education.takeit.oauth.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "oauth.kakao")
@Getter
@Setter
public class KakaoProperties {
	private String clientId;
	private String clientSecret;
	private String redirectUri;
	private String tokenUri;
	private String jwkUri;
	private String scope;
}
