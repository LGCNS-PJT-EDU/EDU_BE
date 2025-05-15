package com.education.takeit.diagnosis.service;

import com.education.takeit.diagnosis.dto.DiagnosisAnswerRequest;
import com.education.takeit.diagnosis.dto.DiagnosisResponse;
import com.education.takeit.diagnosis.dto.GroupedDiagnosisResponse;
import com.education.takeit.diagnosis.repository.DiagnosisRepository;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.roadmap.dto.RoadmapSaveResDto;
import com.education.takeit.roadmap.service.RoadmapService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

  private final DiagnosisRepository diagnosisRepository;
  private final RoadmapService roadmapService;

  /**
   * 진단 질문 조회 메소드
   *
   * @return 진단 질문 리스트
   */
  public GroupedDiagnosisResponse findAllDiagnosis() {
    List<DiagnosisResponse> list =
        diagnosisRepository.findAllWithChoices().stream()
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
                                        choice.getChoiceId(),
                                        choice.getChoiceNum(),
                                        choice.getChoice(),
                                        choice.getValue()))
                            .toList()))
            .toList();

    if (list.isEmpty()) {
      throw new CustomException(StatusCode.DIAGNOSIS_NOT_FOUND);
    }
    return groupDiagnosisByType(list);
  }

  /**
   * 진단 질문 그룹핑(Common,FE,BE) 메소드
   *
   * @param list
   * @return 진단 질문 그룹핑 리스트
   */
  public GroupedDiagnosisResponse groupDiagnosisByType(List<DiagnosisResponse> list) {

    Map<String, List<DiagnosisResponse>> grouped =
        list.stream().collect(Collectors.groupingBy(DiagnosisResponse::questionType));

    return new GroupedDiagnosisResponse(
        grouped.getOrDefault("COMMON", List.of()),
        grouped.getOrDefault("BE", List.of()),
        grouped.getOrDefault("FE", List.of()));
  }

  /**
   * 진단 결과 => 로드맵 반환 메소드
   *
   * @param userId
   * @param answers
   * @return 진단 결과 => 로드맵
   */
  public RoadmapSaveResDto recommendRoadmapByDiagnosis(
      Long userId, List<DiagnosisAnswerRequest> answers) {
    if (answers == null || answers.isEmpty()) {
      throw new CustomException(StatusCode.INVALID_DIAGNOSIS_ANSWER);
    }
    return roadmapService.selectRoadmap(userId, answers);
  }
}
