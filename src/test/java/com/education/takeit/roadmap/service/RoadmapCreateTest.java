package com.education.takeit.roadmap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.education.takeit.diagnosis.dto.DiagnosisAnswerRequest;
import com.education.takeit.roadmap.dto.RoadmapSaveResDto;
import com.education.takeit.roadmap.dto.SubjectDto;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoadmapCreateTest {
  @Mock RoadmapService roadmapService;

  @Test
  @DisplayName("postroadmap() -> 사용자 로드맵 생성 및 반환")
  void createUserRoadmap() {
    // given
    Long userId = null;
    List<DiagnosisAnswerRequest> answers =
        List.of(
            new DiagnosisAnswerRequest(1L, "FE"),
            new DiagnosisAnswerRequest(2L, "2"),
            new DiagnosisAnswerRequest(3L, "2"),
            new DiagnosisAnswerRequest(4L, "Y"),
            new DiagnosisAnswerRequest(5L, "React"),
            new DiagnosisAnswerRequest(6L, "Y"),
            new DiagnosisAnswerRequest(7L, "Y"),
            new DiagnosisAnswerRequest(8L, "N"),
            new DiagnosisAnswerRequest(9L, "Y"),
            new DiagnosisAnswerRequest(10L, "N"));

    RoadmapSaveResDto roadmapSaveResDto =
        new RoadmapSaveResDto(
            "사용자는 uuid가 없어요",
            "사용자 랜덤 로드맵 이름",
            1L,
            List.of(
                new SubjectDto(1L, "HTML", 1),
                new SubjectDto(2L, "CSS", 2),
                new SubjectDto(3L, "JavaScript", 3),
                new SubjectDto(4L, "TypeScript", 4),
                new SubjectDto(5L, "Virtual DOM", 5),
                new SubjectDto(6L, "Git & GitHub", 6),
                new SubjectDto(7L, "Git Hook (Husky, lint-staged) 자동화", 7),
                new SubjectDto(8L, "Axios 인스턴스 관리, 공통 인터셉터 구성", 8),
                new SubjectDto(9L, "REST API 기반 에러 처리 / 재시도 로직", 9),
                new SubjectDto(10L, "React", 10),
                new SubjectDto(11L, "Redux", 11),
                new SubjectDto(12L, "Zustand", 12),
                new SubjectDto(17L, "컴포넌트 디자인 시스템(Storybook)", 17),
                new SubjectDto(18L, "Tailwind CSS", 18),
                new SubjectDto(19L, "SCSS", 19),
                new SubjectDto(20L, "styled", 20),
                new SubjectDto(21L, "EsLint & Prettier", 21),
                new SubjectDto(22L, "Next.js", 22),
                new SubjectDto(23L, "React 렌더링 최적화 (React.memo, useMemo, useCallback)", 23),
                new SubjectDto(24L, "React Query 심화 (Prefetch, Query Keys, Invalidations)", 24),
                new SubjectDto(29L, "Webpack 개념과 설정", 29),
                new SubjectDto(33L, "Unit Test, Snapshot Test, Integration Test", 33)));

    when(roadmapService.selectRoadmap(userId, answers)).thenReturn(roadmapSaveResDto);

    // when
    RoadmapSaveResDto result = roadmapService.selectRoadmap(userId, answers);

    // then
    assertThat(result.uuid()).isEqualTo("사용자는 uuid가 없어요");
    assertThat(result.subjects()).hasSize(22);
    assertThat(result.subjects().getFirst().subjectName()).isEqualTo("HTML");
  }

  @Test
  @DisplayName("postroadmap() -> 게스트 로드맵 임시 저장 및 반환")
  void createGuestRoadmap() {
    // given
    Long userId = 2L;
    List<DiagnosisAnswerRequest> answers =
        List.of(
            new DiagnosisAnswerRequest(1L, "FE"),
            new DiagnosisAnswerRequest(2L, "2"),
            new DiagnosisAnswerRequest(3L, "2"),
            new DiagnosisAnswerRequest(4L, "Y"),
            new DiagnosisAnswerRequest(5L, "React"),
            new DiagnosisAnswerRequest(6L, "Y"),
            new DiagnosisAnswerRequest(7L, "Y"),
            new DiagnosisAnswerRequest(8L, "N"),
            new DiagnosisAnswerRequest(9L, "Y"),
            new DiagnosisAnswerRequest(10L, "N"));

    RoadmapSaveResDto roadmapSaveResDto =
        new RoadmapSaveResDto(
            UUID.fromString("50e8efb9-ef44-41dd-bef7-257ffc760736").toString(),
            "로드맵 이름",
            1L,
            List.of(
                new SubjectDto(1L, "HTML", 1),
                new SubjectDto(2L, "CSS", 2),
                new SubjectDto(3L, "JavaScript", 3),
                new SubjectDto(4L, "TypeScript", 4),
                new SubjectDto(5L, "Virtual DOM", 5),
                new SubjectDto(6L, "Git & GitHub", 6),
                new SubjectDto(7L, "Git Hook (Husky, lint-staged) 자동화", 7),
                new SubjectDto(8L, "Axios 인스턴스 관리, 공통 인터셉터 구성", 8),
                new SubjectDto(9L, "REST API 기반 에러 처리 / 재시도 로직", 9),
                new SubjectDto(10L, "React", 10),
                new SubjectDto(11L, "Redux", 11),
                new SubjectDto(12L, "Zustand", 12),
                new SubjectDto(17L, "컴포넌트 디자인 시스템(Storybook)", 17),
                new SubjectDto(18L, "Tailwind CSS", 18),
                new SubjectDto(19L, "SCSS", 19),
                new SubjectDto(20L, "styled", 20),
                new SubjectDto(21L, "EsLint & Prettier", 21),
                new SubjectDto(22L, "Next.js", 22),
                new SubjectDto(23L, "React 렌더링 최적화 (React.memo, useMemo, useCallback)", 23),
                new SubjectDto(24L, "React Query 심화 (Prefetch, Query Keys, Invalidations)", 24),
                new SubjectDto(29L, "Webpack 개념과 설정", 29),
                new SubjectDto(33L, "Unit Test, Snapshot Test, Integration Test", 33)));

    when(roadmapService.selectRoadmap(userId, answers)).thenReturn(roadmapSaveResDto);

    // when
    RoadmapSaveResDto result = roadmapService.selectRoadmap(userId, answers);

    // then
    assertThat(result.uuid()).isEqualTo("50e8efb9-ef44-41dd-bef7-257ffc760736");
    assertThat(result.subjects()).hasSize(22);
    assertThat(result.subjects().getFirst().subjectName()).isEqualTo("HTML");
  }
}
