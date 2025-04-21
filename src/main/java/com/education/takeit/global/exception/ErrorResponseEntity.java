package com.education.takeit.global.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorResponseEntity {
    private int statusCode;
    private String message;
    private HttpStatus httpStatus;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(StatusCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .httpStatus(errorCode.getHttpStatus())
                        .statusCode(errorCode.getStatusCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}
