package com.education.takeit.interview.dto;

import lombok.Builder;

@Builder
public record InterviewHistoryResDto(String interviewContent,
                                     Long subId,
                                     int nth,
                                     String userReply,
                                     String aiFeedback,
                                     String interviewAnswer) {
}
