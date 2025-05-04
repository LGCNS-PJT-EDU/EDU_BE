package com.education.takeit.roadmap.dto;

import java.util.List;

public record RoadmapResponseDto(
        String roadmapId,
        List<SubjectDto> subjects
) {}
