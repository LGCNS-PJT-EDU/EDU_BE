package com.education.takeit.interview.dto;

import lombok.Builder;

@Builder
public record InterviewHistoryResDto(
    Long interviewId,
    String interviewContent,
    Long subId,
    int nth,
    String userReply,
    String aiFeedback,
    String interviewAnswer,
    String summary,
    String modelAnswer,
    String keyword) {}
