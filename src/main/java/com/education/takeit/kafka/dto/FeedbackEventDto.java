package com.education.takeit.kafka.dto;

public record FeedbackEventDto(Long userId, Long subjectId, String type) {}
