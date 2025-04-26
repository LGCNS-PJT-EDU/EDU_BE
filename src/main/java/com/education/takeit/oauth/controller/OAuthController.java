package com.education.takeit.oauth.controller;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.oauth.dto.OAuthLoginRequest;
import com.education.takeit.oauth.service.GoogleOAuthService;
import com.education.takeit.oauth.service.KakaoOAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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

		String accessToken = tokens.get("accessToken");
		String refreshToken = tokens.get("refreshToken");

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("X-Refresh-Token", refreshToken);

		Message message = new Message(StatusCode.OK);

		return ResponseEntity.ok()
				.headers(headers)
				.body(message);
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

		String accessToken = tokens.get("accessToken");
		String refreshToken = tokens.get("refreshToken");

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("X-Refresh-Token", refreshToken);

		Message message = new Message(StatusCode.OK);

		return ResponseEntity.ok()
				.headers(headers)
				.body(message);
	}
}
