package com.education.takeit.kafka.dto;

public record DlqWrapper(
        FeedbackEventDto original,
        String errorMessage,
        long ts
) {
}
