package com.education.takeit.admin.failLog.dto;

import java.time.LocalDateTime;

public record RecomFailLogDto(
        Long id,
        String email,
        String nickname,
        Long subjectId,
        String errorCode,
        String errorMessage,
        Boolean retry,
        LocalDateTime createdDt
) {}
