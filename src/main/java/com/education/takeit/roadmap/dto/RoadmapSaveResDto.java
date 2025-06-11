package com.education.takeit.roadmap.dto;

import java.util.List;

public record RoadmapSaveResDto(
    String uuid, String roadmapName, Long userLocationSubjectId, List<SubjectDto> subjects) {}
