package com.education.takeit.oauth.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.google")
@Getter
@Setter
public class GoogleProperties {
  private String clientId;
  private String clientSecret;
  private String redirectUri;
}
