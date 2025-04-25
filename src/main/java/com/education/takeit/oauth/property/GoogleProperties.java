package com.education.takeit.oauth.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "oauth.google")
@Getter
@Setter
public class GoogleProperties {
	private String clientId;
	private String clientSecret;
	private String redirectUri;
}
