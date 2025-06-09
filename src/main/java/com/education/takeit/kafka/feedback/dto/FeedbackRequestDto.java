package com.education.takeit.kafka.feedback.dto;

public record FeedbackRequestDto(Long userId, Long subjectId, String type, int nth) {}
