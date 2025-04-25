package com.education.takeit.oauth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.exception.StatusCode;
import com.education.takeit.oauth.dto.OAuthLoginRequest;
import com.education.takeit.oauth.service.GoogleOAuthService;
import com.education.takeit.oauth.service.KakaoOAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "OAuth 소셜 로그인", description = "OAuth 소셜 로그인 API")
public class OAuthController {

	private final GoogleOAuthService googleOAuthService;
	private final KakaoOAuthService kakaoOAuthService;

	/**
	 * Google OAuth 소셜 로그인
	 * @param request
	 * @return
	 */
	@PostMapping("/google/login")
	@Operation(summary = "Google OAuth 소셜 로그인", description = "Google OAuth 소셜 로그인 API")
	public ResponseEntity<Message> loginWithGoogle(@RequestBody OAuthLoginRequest request) {
		Map<String, String> tokens = googleOAuthService.login(request);
		return ResponseEntity.ok(new Message(StatusCode.OK, tokens));
	}

	/**
	 * Kakao OAuth 소셜 로그인
	 * @param request
	 * @return
	 */
	@PostMapping("/kakao/login")
	@Operation(summary = "Kakao OAuth 소셜 로그인", description = "Kakao OAuth 소셜 로그인 API")
	public ResponseEntity<Message> loginWithKakao(@RequestBody OAuthLoginRequest request) {
		Map<String, String> tokens = kakaoOAuthService.login(request);
		return ResponseEntity.ok(new Message(StatusCode.OK, tokens));
	}
}
