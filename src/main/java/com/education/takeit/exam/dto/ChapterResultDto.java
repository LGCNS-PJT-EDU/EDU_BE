package com.education.takeit.exam.dto;

public record ChapterResultDto(
    int chapterNum, String chapterName, boolean weakness, int cnt, int totalCnt) {}
