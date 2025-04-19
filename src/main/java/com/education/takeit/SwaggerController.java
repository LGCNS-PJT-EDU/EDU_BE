package com.education.takeit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/hello")
@Tag(name = "Hello", description = "테스트용 Hello API")
public class SwaggerController {

	@GetMapping
	@Operation(summary = "Hello 메시지 반환", description = "간단한 문자열을 반환하는 테스트 API입니다.")
	public String sayHello() {
		return "Hello, Swagger!";
	}
}
