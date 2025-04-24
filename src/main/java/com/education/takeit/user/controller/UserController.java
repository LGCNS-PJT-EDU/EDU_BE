package com.education.takeit.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.exception.StatusCode;
import com.education.takeit.global.security.JwtUserDetails;
import com.education.takeit.user.dto.ReqSigninDto;
import com.education.takeit.user.dto.ReqSignupDto;
import com.education.takeit.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자와 관련된 API")
public class UserController {
	private final UserService userService;

	@PostMapping("/signup")
	@Operation(summary = "회원가입", description = "자체 서비스 회원가입 API")
	public ResponseEntity<Message> signUp(@RequestBody ReqSignupDto reqSignupDto) {
		userService.signUp(reqSignupDto);
		return ResponseEntity.ok(new Message(StatusCode.OK));
	}

	@PostMapping("/signin")
	@Operation(summary = "로그인", description = "자체 서비스 로그인 API")
	public ResponseEntity<Message> signIn(@RequestBody ReqSigninDto reqSigninDto) {
		String token = userService.signIn(reqSigninDto);
		return ResponseEntity.ok(new Message(StatusCode.OK, token));
	}

	@PostMapping("/signout")
	@Operation(summary = "회원탈퇴", description = "회원 탈퇴 API")
	public ResponseEntity<Message> signOut(@AuthenticationPrincipal JwtUserDetails principal) {
		userService.signOut(principal.getUsername());
		return ResponseEntity.ok(new Message(StatusCode.OK));
	}
}
