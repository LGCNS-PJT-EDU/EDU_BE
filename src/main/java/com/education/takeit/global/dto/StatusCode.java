package com.education.takeit.global.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum StatusCode {
	OK(200, "요청이 성공적으로 처리되었습니다.", HttpStatus.OK),
	ALREADY_EXIST_EMAIL(409, "이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
	NOT_EXIST_USER(404, "아이디 또는 비밀번호를 다시 확인해주세요.", HttpStatus.NOT_FOUND),
	NOT_SUPPORT_LOCAL_LOGIN(403, "해당 계정은 소셜로그인 전용입니다.", HttpStatus.FORBIDDEN),
	USER_NOT_FOUND(404, "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND),
	INVALID_KAKAO_ID_TOKEN(401, "유효하지 않은 KAKAO ID 토큰입니다.", HttpStatus.UNAUTHORIZED),
	INVALID_GOOGLE_ID_TOKEN(401, "유효하지 않은 GOOGLE ID 토큰입니다.", HttpStatus.UNAUTHORIZED),
	MISSING_NAVER_STATE(401,"NAVER STATE 값이 비어있습니다.", HttpStatus.BAD_REQUEST),
	UNAUTHORIZED(401,"만료된 토큰입니다.", HttpStatus.UNAUTHORIZED);

	private final int statusCode;
	private final String message;
	private final HttpStatus httpStatus;

	StatusCode(int statusCode, String message, HttpStatus httpStatus) {
		this.statusCode = statusCode;
		this.message = message;
		this.httpStatus = httpStatus;
	}
}
