package com.education.takeit.feedback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record FeedbackResponseDto(
    InfoDto info,
    @JsonProperty("scores") Map<String, Integer> scores,
    @JsonProperty("feedback") FeedbackDto feedback) {}
