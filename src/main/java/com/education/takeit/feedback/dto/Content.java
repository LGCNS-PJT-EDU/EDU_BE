package com.education.takeit.feedback.dto;

import java.util.List;

public record Content(List<Subject> subjects, List<Comment> strengths, List<Comment> weaknesses) {}
