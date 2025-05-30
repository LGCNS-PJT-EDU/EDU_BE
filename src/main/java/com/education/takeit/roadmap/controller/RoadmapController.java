package com.education.takeit.roadmap.controller;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.security.CustomUserDetails;
import com.education.takeit.roadmap.dto.*;
import com.education.takeit.roadmap.service.RoadmapService;
import com.education.takeit.roadmap.service.RoadmapTransactionalService;
import com.education.takeit.roadmap.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roadmap")
@Tag(name = "로드맵", description = "로드맵 관련 API")
public class RoadmapController {
  private final RoadmapService roadmapService;
  private final SubjectService subjectService;
  private final RoadmapTransactionalService roadmapTransactionalService;

  /*  @PostMapping("/create")
  @Operation(
      summary = "진단 결과로 로드맵 요청",
      description = "게스트일 경우 uuid와 함께 로드맵 반환, 로그인한 사용자일 경우 로드맵 저장 및 반환")
  public RoadmapResponseDto getRoadmap(
      @RequestHeader(value = "accessToken", required = false) String flag,
      @RequestBody List<DiagnosisAnswerRequest> answers) {
    return roadmapService.roadmapSelect(flag, answers);
  }
  */

  @GetMapping("/progress")
  @Operation(summary = "마이페이지에서 로드맵 진척도 조회", description = "전체 과목 중 이수한 과목 수 백분율로 계산해서 반환하는 API")
  public ResponseEntity<Message> getUserProgress(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = userDetails.getUserId();
    MyPageResDto response = roadmapService.getProgressPercentage(userId);
    return ResponseEntity.ok(new Message(StatusCode.OK, response));
  }

  @PutMapping("/user")
  @Operation(summary = "로드맵 수정", description = "사용자가 원하는 대로 로드맵 과목 수정 API")
  public ResponseEntity<Message> updateRoadmap(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody List<SubjectDto> subjects) {
    Long userId = userDetails.getUserId();
    roadmapService.updateRoadmap(userId, subjects);

    return ResponseEntity.ok(new Message(StatusCode.OK));
  }

  @DeleteMapping("/user")
  @Operation(summary = "로드맵 삭제", description = "로드맵 삭제 API")
  public ResponseEntity<Message> deleteRoadmap(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    {
      Long userId = userDetails.getUserId();
      roadmapTransactionalService.deleteRoadmap(userId);
      return ResponseEntity.ok(new Message(StatusCode.OK));
    }
  }

  @GetMapping("/default")
  @Operation(summary = "기본 로드맵 제공", description = "기본 로드맵 제공 API")
  public ResponseEntity<Message> findDefaultRoadmap(
      @RequestParam("roadmap") String defaultRoadmapType) {
    List<SubjectDto> defaultRoadmap = roadmapService.getDefaultRoadmap(defaultRoadmapType);
    return ResponseEntity.ok(new Message(StatusCode.OK, defaultRoadmap));
  }

  @PostMapping("/default")
  @Operation(summary = "기본 로드맵을 사용자에게 할당", description = "기본 로드맵을 사용자에게 할당하는 API")
  public ResponseEntity<Message> saveDefaultRoadmap(
      @RequestParam("roadmap") String defaultRoadmapType,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = userDetails.getUserId();
    RoadmapSaveResDto roadmapSaveResDto =
        roadmapService.saveDefaultRoadmap(defaultRoadmapType, userId);
    return ResponseEntity.ok(new Message(StatusCode.OK, roadmapSaveResDto));
  }

  @GetMapping("/subject")
  @Operation(summary = "사용자 과목 정보 제공", description = "사용자가 과목을 눌렀을 때 필요한 정보 제공하는 API")
  public ResponseEntity<Message> findUserRoadmap(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam("subjectId") Long subjectId) {
    Long userId = userDetails.getUserId();
    SubjectFindResDto subjectFindResDto = subjectService.findUserSubject(userId, subjectId);
    return ResponseEntity.ok(new Message(StatusCode.OK, subjectFindResDto));
  }

  @GetMapping
  @Operation(summary = "로드맵 제공", description = "게스트, 사용자의 로드맵 정보 반환")
  public ResponseEntity<Message> findRoadmap(
      @AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("uuid") String uuid) {
    Long userId = (userDetails != null) ? userDetails.getUserId() : null;
    RoadmapFindResDto roadmapFindResDto = roadmapService.findRoadmap(userId, uuid);
    return ResponseEntity.ok(new Message(StatusCode.OK, roadmapFindResDto));
  }
}
