package com.education.takeit.kafka.feedback.dto;

import com.education.takeit.feedback.dto.FeedbackResponseDto;

public record FeedbackResultDto(
    Long userId, Long subjectId, String type, int nth, FeedbackResponseDto feedback) {}
