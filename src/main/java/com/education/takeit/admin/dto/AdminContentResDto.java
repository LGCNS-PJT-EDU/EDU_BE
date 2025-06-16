package com.education.takeit.admin.dto;

public record AdminContentResDto(
    Long totalContentId,
    String contentTitle,
    String contentUrl,
    String contentType,
    String contentPlatform,
    String subName,
    Long userCount) {}
