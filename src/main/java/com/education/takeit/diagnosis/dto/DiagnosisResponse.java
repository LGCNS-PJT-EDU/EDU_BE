package com.education.takeit.diagnosis.dto;

import java.util.List;

public record DiagnosisResponse(
    Long diagnosisId, String question, String questionType, List<ChoiceDto> choices) {

  public record ChoiceDto(Long choiceId, int choiceNum, String choice, String value) {}
}
