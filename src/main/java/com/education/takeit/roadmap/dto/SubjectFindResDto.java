package com.education.takeit.roadmap.dto;

import com.education.takeit.recommend.dto.UserContentResDto;
import java.util.List;

public record SubjectFindResDto(
    Long roadmapId,
    String subject_name,
    String subject_overview,
    List<ChapterFindDto> chapters,
    int preSubmitCount,
    int postSubmitCount,
    List<UserContentResDto> recommendContents) {}
