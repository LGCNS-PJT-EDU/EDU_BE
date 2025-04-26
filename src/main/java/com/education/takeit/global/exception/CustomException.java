package com.education.takeit.global.exception;

import com.education.takeit.global.dto.StatusCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final StatusCode statusCode;

    public CustomException(StatusCode statusCode) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
    }
}
