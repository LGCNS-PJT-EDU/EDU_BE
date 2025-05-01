package com.education.takeit;

import com.education.takeit.oauth.property.GoogleProperties;
import com.education.takeit.oauth.property.KakaoProperties;
import com.education.takeit.oauth.property.NaverProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties({
  GoogleProperties.class,
  KakaoProperties.class,
  NaverProperties.class
})
public class TakeitApplication {

  public static void main(String[] args) {
    SpringApplication.run(TakeitApplication.class, args);
  }
}
