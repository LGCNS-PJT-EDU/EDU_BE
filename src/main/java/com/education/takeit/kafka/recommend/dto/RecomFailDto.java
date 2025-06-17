package com.education.takeit.kafka.recommend.dto;

public record RecomFailDto(Long userId, Long subjectId, String errorCode, String errorMessage) {}
