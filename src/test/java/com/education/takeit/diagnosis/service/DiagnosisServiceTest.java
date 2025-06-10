package com.education.takeit.diagnosis.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.education.takeit.diagnosis.dto.DiagnosisAnswerRequest;
import com.education.takeit.diagnosis.dto.DiagnosisResponse;
import com.education.takeit.diagnosis.dto.GroupedDiagnosisResponse;
import com.education.takeit.diagnosis.entity.Choice;
import com.education.takeit.diagnosis.entity.Diagnosis;
import com.education.takeit.diagnosis.repository.DiagnosisRepository;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.roadmap.dto.RoadmapSaveResDto;
import com.education.takeit.roadmap.dto.SubjectDto;
import com.education.takeit.roadmap.service.RoadmapService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiagnosisServiceTest {

  @Mock DiagnosisRepository diagnosisRepository;

  @Mock RoadmapService roadmapService;

  @InjectMocks DiagnosisService diagnosisService;

  @Test
  @DisplayName("getDiagnosis() → COMMON, FE, BE 타입에 따라 그룹핑되어 반환된다.")
  void 진단_질문_조회() {
    // given
    Diagnosis common =
        Diagnosis.builder()
            .diagnosisId(1L)
            .question("공통 질문")
            .questionType("COMMON")
            .choices(
                List.of(
                    Choice.builder().choiceId(1L).choiceNum(1).choice("프론트엔드").value("FE").build(),
                    Choice.builder().choiceId(2L).choiceNum(2).choice("백엔드").value("BE").build()))
            .build();

    Diagnosis fe =
        Diagnosis.builder()
            .diagnosisId(2L)
            .question("FE 질문")
            .questionType("FE")
            .choices(
                List.of(
                    Choice.builder()
                        .choiceId(3L)
                        .choiceNum(1)
                        .choice("React")
                        .value("React")
                        .build()))
            .build();

    Diagnosis be =
        Diagnosis.builder()
            .diagnosisId(3L)
            .question("BE 질문")
            .questionType("BE")
            .choices(
                List.of(
                    Choice.builder()
                        .choiceId(4L)
                        .choiceNum(1)
                        .choice("Spring")
                        .value("Java/Spring")
                        .build()))
            .build();

    when(diagnosisRepository.findAllWithChoices()).thenReturn(List.of(common, fe, be));

    // when
    GroupedDiagnosisResponse result = diagnosisService.findAllDiagnosis(null);

    // then
    assertThat(result.COMMON()).hasSize(1);
    assertThat(result.FE()).hasSize(1);
    assertThat(result.BE()).hasSize(1);

    assertThat(result.COMMON().get(0).question()).isEqualTo("공통 질문");
    assertThat(result.FE().get(0).choices().get(0).value()).isEqualTo("React");
    assertThat(result.BE().get(0).choices().get(0).value()).isEqualTo("Java/Spring");
  }

  @Test
  void 진단_질문이_없을_떄_예외_발생() {
    // Given
    when(diagnosisRepository.findAllWithChoices()).thenReturn(Collections.emptyList());

    // When & Then
    assertThatThrownBy(() -> diagnosisService.findAllDiagnosis(null))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(StatusCode.DIAGNOSIS_NOT_FOUND.getMessage());
  }

  @Test
  void 진단_질문_그룹핑_성공() {
    // Given
    DiagnosisResponse common =
        new DiagnosisResponse(
            1L, "공통질문", "COMMON", List.of(new DiagnosisResponse.ChoiceDto(1L, 1, "질문 선택지", "0")));
    DiagnosisResponse fe =
        new DiagnosisResponse(
            1L, "FE질문", "FE", List.of(new DiagnosisResponse.ChoiceDto(1L, 1, "질문 선택지", "0")));
    DiagnosisResponse be =
        new DiagnosisResponse(
            1L, "BE질문", "BE", List.of(new DiagnosisResponse.ChoiceDto(1L, 1, "질문 선택지", "0")));

    // When
    GroupedDiagnosisResponse result =
        diagnosisService.groupDiagnosisByType(List.of(common, fe, be), false);

    // Then
    assertThat(result.COMMON()).hasSize(1);
    assertThat(result.FE()).hasSize(1);
    assertThat(result.BE()).hasSize(1);
  }

  @Test
  @DisplayName("postDiagnosis() → 진단 결과 기반 추천 로드맵 반환")
  void 진단_결과로_로드맵_반환() {
    // given
    Long userId = 1L;
    List<DiagnosisAnswerRequest> answers =
        List.of(
            new DiagnosisAnswerRequest(1L, "BE"),
            new DiagnosisAnswerRequest(2L, "2"),
            new DiagnosisAnswerRequest(3L, "3"),
            new DiagnosisAnswerRequest(4L, "Y"),
            new DiagnosisAnswerRequest(11L, "Python/Flask"),
            new DiagnosisAnswerRequest(12L, "Y"),
            new DiagnosisAnswerRequest(13L, "Y"),
            new DiagnosisAnswerRequest(14L, "Y"),
            new DiagnosisAnswerRequest(15L, "N"));

    RoadmapSaveResDto roadmap =
        new RoadmapSaveResDto(
            UUID.fromString("cc6d893c-637f-44ce-9a82-69c7137b3a81").toString(),
            "로드맵 이름"
            ,
            1L,
            List.of(
                new SubjectDto(35L, "리눅스 명령어", 1),
                new SubjectDto(36L, "HTTP, HTTPS, DNS, TCP/IP 기본 개념", 2),
                new SubjectDto(37L, "Git & GitHub", 3),
                new SubjectDto(38L, "Git Hook (Husky, lint-staged) 자동화", 4),
                new SubjectDto(40L, "Python", 6),
                new SubjectDto(43L, "SQL문", 9),
                new SubjectDto(44L, "More About Database(RDB 종류, NoSQL)", 10),
                new SubjectDto(45L, "Scaling Databases(쿼리튜닝, 정규화)", 11),
                new SubjectDto(49L, "Flask", 15),
                new SubjectDto(57L, "Django 운영 & 배포", 23)));

    when(roadmapService.selectRoadmap(userId, answers)).thenReturn(roadmap);

    // when
    RoadmapSaveResDto result = diagnosisService.recommendRoadmapByDiagnosis(userId, answers);

    // then
    assertThat(result.uuid().toString()).isEqualTo("cc6d893c-637f-44ce-9a82-69c7137b3a81");
    assertThat(result.subjects()).hasSize(10);
    assertThat(result.subjects().get(0).subjectName()).isEqualTo("리눅스 명령어");
  }

  @Test
  void 진단_결과가_없을_때_예외_발생() {
    // Given
    Long userId = 1L;
    List<DiagnosisAnswerRequest> answers = Collections.emptyList();

    // When & Then
    assertThatThrownBy(() -> diagnosisService.recommendRoadmapByDiagnosis(userId, answers))
        .isInstanceOf(CustomException.class)
        .hasMessageContaining(StatusCode.INVALID_DIAGNOSIS_ANSWER.getMessage());
  }
}
