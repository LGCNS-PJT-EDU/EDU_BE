package com.education.takeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TakeitApplication {

	public static void main(String[] args) {
		SpringApplication.run(TakeitApplication.class, args);
	}

}
