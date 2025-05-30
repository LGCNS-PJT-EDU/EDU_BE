package com.education.takeit.roadmap.dto;

import java.util.List;

public record SubjectFindResDto(
    String subject_name,
    String subject_overview,
    List<ChapterFindDto> chapters,
    int preSubmitCount,
    int postSubmitCount,
    List<RecommendContentsFindDto> recommendContents) {}
