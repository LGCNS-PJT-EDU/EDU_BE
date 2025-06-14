package com.education.takeit.admin.failLog.dto;

import java.time.LocalDateTime;

public record FeedbackFailLogDto(Long id,
                                 String email,
                                 String nickname,
                                 Long subjectId,
                                 String type,
                                 Integer nth,
                                 String errorCode,
                                 String errorMessage,
                                 Boolean retry,
                                 LocalDateTime createdDt) {

}
