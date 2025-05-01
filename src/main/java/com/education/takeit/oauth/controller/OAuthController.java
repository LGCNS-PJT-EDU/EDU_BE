package com.education.takeit.oauth.controller;

import com.education.takeit.oauth.service.NaverOAuthService;
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
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "OAuth 소셜 로그인", description = "OAuth 소셜 로그인 API")
public class OAuthController {

	private final GoogleOAuthService googleOAuthService;
	private final KakaoOAuthService kakaoOAuthService;
	private final NaverOAuthService naverOAuthService;

	/**
	 * Google OAuth 소셜 로그인
	 * @param request
	 * @return
	 */
	@PostMapping("/google/login")
	@Operation(summary = "Google OAuth 소셜 로그인", description = "Google OAuth 소셜 로그인 API")
	public ResponseEntity<Message> loginWithGoogle(@RequestBody OAuthLoginRequest request) {
		String accessToken = googleOAuthService.login(request);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);

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
		String accessToken = kakaoOAuthService.login(request);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);

		Message message = new Message(StatusCode.OK);

		return ResponseEntity.ok()
				.headers(headers)
				.body(message);
	}

	@PostMapping("/naver/login")
	@Operation(summary = "Naver OAuth 소셜 로그인", description = "Naver OAuth 소셜 로그인 API")
	public ResponseEntity<Message> loginWithNaver(@RequestBody OAuthLoginRequest request) {
		String accessToken = naverOAuthService.login(request);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);

		Message message = new Message(StatusCode.OK);

		return ResponseEntity.ok()
				.headers(headers)
				.body(message);
	}
}
