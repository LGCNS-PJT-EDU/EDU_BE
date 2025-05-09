package com.education.takeit.feedback.dto;

import java.util.List;

public record ContentDto(List<SubjectDto> subjectDtos, List<CommentDto> strengths, List<CommentDto> weaknesses) {}
