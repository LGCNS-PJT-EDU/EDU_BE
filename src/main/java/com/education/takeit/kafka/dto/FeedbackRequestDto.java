package com.education.takeit.kafka.dto;

public record FeedbackRequestDto(Long userId, Long subjectId, String type, int nth) {}
