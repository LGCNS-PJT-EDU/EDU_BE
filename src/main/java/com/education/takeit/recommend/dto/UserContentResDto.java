package com.education.takeit.recommend.dto;

public record UserContentResDto(
    Long contentId,
    Long subjectId,
    String title,
    String url,
    String type,
    String platform,
    String duaration,
    String price,
    Boolean isAiRecommendation,
    String comment) {}
