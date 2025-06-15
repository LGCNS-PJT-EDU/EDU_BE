package com.education.takeit.admin.dto;

public record AdminExamResDto(
    Long examId,
    String examContent,
    int examAnswer,
    String examLevel,
    String option1,
    String option2,
    String option3,
    String option4,
    String solution,
    Long userCount,
    String subName) {}
