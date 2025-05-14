package com.education.takeit.feedback.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public record InfoDto(
    @JsonProperty("userId") String userId,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") @JsonProperty("date")
        LocalDate date,
    @JsonProperty("subject") String subject) {}
