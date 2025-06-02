package com.education.takeit.interview.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserInterviewReplyReqDto(
        Long interviewId,
        String userReply
) {}