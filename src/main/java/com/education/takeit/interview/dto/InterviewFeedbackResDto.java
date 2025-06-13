package com.education.takeit.interview.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record InterviewFeedbackResDto(
    @JsonProperty("comment") String comment,
    @JsonProperty("concept_summary") String conceptSummary,
    @JsonProperty("model_answer") String modelAnswer,
    @JsonProperty("recommend_keywords") List<String> recommendKeywords) {}
