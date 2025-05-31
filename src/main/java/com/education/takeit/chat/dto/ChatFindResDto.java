package com.education.takeit.chat.dto;

import java.time.LocalDateTime;

public record ChatFindResDto(String userMessage, String aiMessage, LocalDateTime chatTimestamp) {
}
