package com.education.takeit.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StatusCode {
    OK(200, "요청이 성공적으로 처리되었습니다.", HttpStatus.OK),
    ALREADY_EXIST_USERID(409, "이미 존재하는 아이디입니다.", HttpStatus.CONFLICT),
    ALREADY_EXIST_EMAIL(409, "이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
    NOT_EXIST_USER(404, "아이디 또는 비밀번호를 다시 확인해주세요.", HttpStatus.NOT_FOUND),
    NOT_SUPPORT_LOCAL_LOGIN(403, "해당 계정은 소셜로그인 전용입니다.", HttpStatus.FORBIDDEN);

    private final int statusCode;
    private final String message;
    private final HttpStatus httpStatus;

    StatusCode(int statusCode, String message, HttpStatus httpStatus) {
        this.statusCode = statusCode;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
