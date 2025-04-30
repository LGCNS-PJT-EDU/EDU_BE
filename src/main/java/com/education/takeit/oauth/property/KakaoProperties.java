package com.education.takeit.oauth.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
