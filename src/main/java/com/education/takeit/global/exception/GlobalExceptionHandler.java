package com.education.takeit.global.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> {
      errors.put(error.getField(), error.getDefaultMessage());
    });
    return ResponseEntity.badRequest().body(errors);
  }
}
