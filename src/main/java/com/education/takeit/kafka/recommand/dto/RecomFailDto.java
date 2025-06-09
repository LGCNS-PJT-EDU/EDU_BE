package com.education.takeit.kafka.recommand.dto;

public record RecomFailDto(Long userId, Long subjectId, String errorCode, String errorMessage) {}
