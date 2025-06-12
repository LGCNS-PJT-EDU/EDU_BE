package com.education.takeit.feedback.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;

public record FeedbackFindResponseDto(
        @JsonProperty("userId") Long userId,
        @JsonFormat(shape = JsonFormat.Shape.STRING) @JsonProperty("date")
        LocalDateTime date,
        @JsonProperty("subject") String subject,
        @JsonProperty("scores") Map<String, Integer> scores,
        @JsonProperty("feedback") FeedbackDto feedback) {}
