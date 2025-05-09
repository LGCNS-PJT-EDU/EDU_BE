package com.education.takeit.exam.dto;

public record ExamAnswerDto(
    Long id,
    int chapterNum,
    String chapterName,
    String difficulty,
    boolean answerTF,
    int userAnswer) {}
