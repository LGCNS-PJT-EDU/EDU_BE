package com.education.takeit.diagnosis.service;

import com.education.takeit.diagnosis.dto.DiagnosisResponse;
import com.education.takeit.diagnosis.repository.DiagnosisRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

  private final DiagnosisRepository diagnosisRepository;

  public List<DiagnosisResponse> getDiagnosis() {
    return diagnosisRepository.findAllWithChoices().stream()
        .map(
            diagnosis ->
                new DiagnosisResponse(
                    diagnosis.getDiagnosisId(),
                    diagnosis.getQuestion(),
                    diagnosis.getQuestionType(),
                    diagnosis.getChoices().stream()
                        .map(
                            choice ->
                                new DiagnosisResponse.ChoiceDto(
                                    choice.getChoiceId(), choice.getChoice(), choice.getValue()))
                        .toList()))
        .toList();
  }
}
