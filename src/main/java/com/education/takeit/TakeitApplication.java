package com.education.takeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

import com.education.takeit.oauth.property.GoogleProperties;
import com.education.takeit.oauth.property.KakaoProperties;

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties({
	GoogleProperties.class,
	KakaoProperties.class
})
public class TakeitApplication {

	public static void main(String[] args) {
		SpringApplication.run(TakeitApplication.class, args);
	}

}
