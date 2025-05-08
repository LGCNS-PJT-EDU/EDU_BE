package com.education.takeit.diagnosis.service;

import com.education.takeit.diagnosis.dto.DiagnosisAnswerRequest;
import com.education.takeit.diagnosis.dto.DiagnosisResponse;
import com.education.takeit.diagnosis.dto.GroupedDiagnosisResponse;
import com.education.takeit.diagnosis.repository.DiagnosisRepository;
import com.education.takeit.roadmap.dto.RoadmapResponseDto;
import com.education.takeit.roadmap.service.RoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

  private final DiagnosisRepository diagnosisRepository;
  private final RoadmapService roadmapService;

  public GroupedDiagnosisResponse getDiagnosis() {
    List<DiagnosisResponse> list = diagnosisRepository.findAllWithChoices().stream()
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
                                                                    choice.getChoiceId(), choice.getChoiceNum(), choice.getChoice(), choice.getValue()))
                                            .toList()))
            .toList();

    return groupingDiagnosis(list);
  }

  public GroupedDiagnosisResponse groupingDiagnosis(List<DiagnosisResponse> list) {

    Map<String, List<DiagnosisResponse>> grouped = list.stream()
            .collect(Collectors.groupingBy(DiagnosisResponse::questionType));

    return new GroupedDiagnosisResponse(
            grouped.getOrDefault("COMMON", List.of()),
            grouped.getOrDefault("BE", List.of()),
            grouped.getOrDefault("FE", List.of())
    );
  }

  public RoadmapResponseDto postDiagnosis(String flag, List<DiagnosisAnswerRequest> answers) {
    return roadmapService.roadmapSelect(flag, answers);
  }
}
