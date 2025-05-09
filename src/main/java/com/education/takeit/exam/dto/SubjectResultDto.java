package com.education.takeit.exam.dto;

public record SubjectResultDto(
    Long subjectId, String startDate, Long duration, int submitCnt, int level, int cnt, int totalCnt) {}
