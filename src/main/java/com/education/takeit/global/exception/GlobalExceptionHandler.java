package com.education.takeit.global.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponseEntity> handleCustomException(CustomException e) {
        e.printStackTrace();
        return ErrorResponseEntity.toResponseEntity(e.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    protected RuntimeException handleException(Exception e) {
        e.printStackTrace();
        return new RuntimeException(e);
    }
}