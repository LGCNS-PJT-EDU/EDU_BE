package com.education.takeit.global.config;

import com.education.takeit.oauth.property.GoogleProperties;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleOAuthConfig {

  @Bean
  public GoogleIdTokenVerifier googleIdTokenVerifier(GoogleProperties properties) {
    return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
        .setAudience(Collections.singletonList(properties.getClientId()))
        .build();
  }
}
