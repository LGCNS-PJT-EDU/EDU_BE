package com.education.takeit.interview.dto;

import java.util.List;

public record InterviewAllReplyReqDto(List<AiFeedbackReqDto> answers, int nth) {}
