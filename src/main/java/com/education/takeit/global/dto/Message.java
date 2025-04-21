package com.education.takeit.global.dto;

import com.education.takeit.global.exception.StatusCode;

public record Message(int stateCode, String message, Object data) {
    public static final String DEFAULT_RESPONSE = "Request processed successfully";

    public Message(StatusCode statusCode, Object data){
        this(statusCode.getStatusCode(), statusCode.getMessage(), data);
    }

    public Message(StatusCode statusCode){
        this(statusCode.getStatusCode(), statusCode.getMessage(), DEFAULT_RESPONSE);
    }
}
