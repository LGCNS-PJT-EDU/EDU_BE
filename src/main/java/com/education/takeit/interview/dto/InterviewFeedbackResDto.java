package com.education.takeit.interview.dto;

import java.util.List;

public record InterviewFeedbackResDto(
        Long interviewId,
        String userReply,
        int nth,
        String aiFeedback
) {}
