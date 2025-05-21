package com.education.takeit.recommend.dto;

public record UserContentResDto(
    String contentTitle,
    String contentUrl,
    String contentType,
    String contentPlatform,
    String contentDuration,
    String contentPrice) {}
