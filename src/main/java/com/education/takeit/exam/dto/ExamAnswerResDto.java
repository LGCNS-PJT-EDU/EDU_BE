package com.education.takeit.exam.dto;

import java.util.List;

public record ExamAnswerResDto(
    Long subjectId, String startDate, Long duration, int submitCnt, List<ExamAnswerDto> answers) {}
