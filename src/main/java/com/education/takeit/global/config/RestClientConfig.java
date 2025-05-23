package com.education.takeit.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestClient;

@EnableRetry
@Configuration
public class RestClientConfig {

  @Bean
  public RestClient restClient(RestClient.Builder builder) {
    return builder.build();
  }
}
