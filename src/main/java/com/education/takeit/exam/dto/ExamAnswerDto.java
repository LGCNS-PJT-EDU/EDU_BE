package com.education.takeit.exam.dto;

public record ExamAnswerDto(
    Long examId,
    String examContent,
    int chapterNum,
    String chapterName,
    String difficulty,
    boolean answerTF,
    int userAnswer) {}
