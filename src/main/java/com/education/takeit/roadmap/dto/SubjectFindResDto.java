package com.education.takeit.roadmap.dto;

import com.education.takeit.recommend.dto.UserContentResDto;
import java.util.List;
// 로드맵 id 널어주기
public record SubjectFindResDto(
    String subject_name,
    String subject_overview,
    List<ChapterFindDto> chapters,
    int preSubmitCount,
    int postSubmitCount,
    List<UserContentResDto> recommendContents) {}
