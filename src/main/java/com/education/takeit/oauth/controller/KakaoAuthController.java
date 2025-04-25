//package com.education.takeit.oauth.controller;
//
//import com.education.takeit.global.dto.Message;
//import com.education.takeit.global.exception.StatusCode;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.education.takeit.oauth.dto.KakaoLoginRequest;
//import com.education.takeit.oauth.service.KakaoOAuthService;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/auth/kakao")
//@RequiredArgsConstructor
//public class KakaoAuthController {
//
//	private final KakaoOAuthService kakaoOAuthService;
//
//	@PostMapping("/login")
//	public ResponseEntity<Message> login(@RequestBody KakaoLoginRequest request) {
//		String token = kakaoOAuthService.loginWithKakao(request);
//		return ResponseEntity.ok(new Message(StatusCode.OK, token));
//	}
//}
//
