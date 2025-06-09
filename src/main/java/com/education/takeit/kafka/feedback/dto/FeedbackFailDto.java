package com.education.takeit.kafka.feedback.dto;

public record FeedbackFailDto(
    Long userId, Long subjectId, String type, int nth, String errorCode, String errorMessage) {}
