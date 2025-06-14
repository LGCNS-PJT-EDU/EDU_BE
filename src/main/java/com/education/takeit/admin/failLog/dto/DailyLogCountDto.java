package com.education.takeit.admin.failLog.dto;

import java.time.LocalDate;

public record DailyLogCountDto(
        LocalDate date,
        long count
) {
}
