package com.education.takeit.feedback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record FeedbackDto(
    Map<String, String> strength,
    Map<String, String> weakness,
    @JsonProperty("final") String finalComment) {}
