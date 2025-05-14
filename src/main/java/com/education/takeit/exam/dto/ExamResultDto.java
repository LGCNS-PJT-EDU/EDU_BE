package com.education.takeit.exam.dto;

import java.util.List;

public record ExamResultDto(
    Long userId,
    SubjectResultDto subject,
    List<ChapterResultDto> chapters,
    List<ExamAnswerDto> questions) {}
