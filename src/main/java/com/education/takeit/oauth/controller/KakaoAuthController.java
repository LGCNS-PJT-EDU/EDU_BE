package com.education.takeit.oauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.education.takeit.oauth.dto.JwtResponse;
import com.education.takeit.oauth.dto.KakaoLoginRequest;
import com.education.takeit.oauth.service.KakaoOAuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

	private final KakaoOAuthService kakaoOAuthService;

	@PostMapping("/login")
	public ResponseEntity<JwtResponse> login(@RequestBody KakaoLoginRequest request) {
		String jwt = kakaoOAuthService.loginWithKakao(request.getCode());
		return ResponseEntity.ok(new JwtResponse(jwt));
	}
}

