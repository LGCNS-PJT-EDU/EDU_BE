package com.education.takeit.oauth.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "kakao")
@Component
@Getter
@Setter
public class KakaoProperties {
	private String clientId;
	private String clientSecret;
	private String redirectUri;
	private String tokenUri;
	private String jwkUri;
}
