package com.education.takeit.global.dto;

public record Message<T>(int stateCode, String message, T data) {
  public static final String DEFAULT_RESPONSE = "Request processed successfully";

  public Message(StatusCode statusCode, T data) {
    this(statusCode.getStatusCode(), statusCode.getMessage(), data);
  }

  public Message(StatusCode statusCode) {
    this(statusCode.getStatusCode(), statusCode.getMessage(), (T) DEFAULT_RESPONSE);
  }
}
