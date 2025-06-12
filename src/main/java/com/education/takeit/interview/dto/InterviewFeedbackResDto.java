package com.education.takeit.interview.dto;

import java.util.List;

public record InterviewFeedbackResDto(
   String comment, String conceptSummary,  String modelAnswer,
   List<String> recommendKeywords) {}
