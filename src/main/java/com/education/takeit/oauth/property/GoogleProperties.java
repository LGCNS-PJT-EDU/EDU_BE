package com.education.takeit.oauth.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "oauth.google")
@Component
@Getter
@Setter
public class GoogleProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
