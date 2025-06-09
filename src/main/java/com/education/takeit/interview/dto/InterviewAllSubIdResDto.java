package com.education.takeit.interview.dto;

import java.util.List;

public record InterviewAllSubIdResDto(
    List<SubjectInfo> existingSubjectIds, List<SubjectInfo> missingSubjectIds) {}
