package com.education.takeit.interview.dto;

public record InterviewFeedbackResDto(
    Long interviewId, String userReply, int nth, String aiFeedback) {}
