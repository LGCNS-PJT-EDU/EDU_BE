package com.education.takeit.roadmap.dto;

import java.util.List;

public record RoadmapFindResDto(
    List<SubjectDto> subjects, String roadmapName, Long userLocationSubjectId) {}
