package com.education.takeit.recommend.dto;

public record UserContentResDto(
    Long totalContentId,
    Long subjectId,
    String contentTitle,
    String contentUrl,
    String contentType,
    String contentPlatform,
    String contentDuration,
    String contentPrice,
    Boolean isAiRecommended) {}
