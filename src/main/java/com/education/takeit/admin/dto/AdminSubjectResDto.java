package com.education.takeit.admin.dto;

public record AdminSubjectResDto(
        Long subId,
        String subNm,
        String subType,
        String subEssential,
        int baseSubOrder,
        Long assignmentCount // 할당 수
) {
}
