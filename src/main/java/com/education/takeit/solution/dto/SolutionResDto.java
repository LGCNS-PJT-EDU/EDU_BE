package com.education.takeit.solution.dto;

public record SolutionResDto(
    boolean isPre,
    String subNm,
    String examContent,
    String option1,
    String option2,
    String option3,
    String option4,
    int examAnswer,
    int userAnswer,
    String solution,
    String examLevel) {}
