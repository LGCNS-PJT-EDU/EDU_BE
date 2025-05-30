package com.education.takeit.diagnosis.controller;

import com.education.takeit.diagnosis.dto.DiagnosisAnswerRequest;
import com.education.takeit.diagnosis.dto.GroupedDiagnosisResponse;
import com.education.takeit.diagnosis.service.DiagnosisService;
import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.security.CustomUserDetails;
import com.education.takeit.roadmap.dto.RoadmapSaveResDto;
import com.education.takeit.roadmap.service.RoadmapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diagnosis")
@Tag(name = "진단", description = "진단과 관련된 API")
public class DiagnosisController {

  private final DiagnosisService diagnosisService;
  private final RoadmapService roadmapService;

  /**
   * 진단 문제 GET 요청(전체)
   *
   * @return
   */
  @GetMapping
  @Operation(summary = "진단 문제 리스트 요청", description = "진단 문제 GET API")
  public ResponseEntity<Message<GroupedDiagnosisResponse>> findAllDiagnosis(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = (userDetails != null) ? userDetails.getUserId() : null;
    GroupedDiagnosisResponse result = diagnosisService.findAllDiagnosis(userId);
    return ResponseEntity.ok(new Message<>(StatusCode.OK, result));
  }

  /**
   * 진단 결과 POST 요청
   *
   * @param answers
   * @return
   */
  @PostMapping
  @Operation(summary = "진단 결과 응답", description = "진단 결과 응답 POST API")
  public ResponseEntity<Message<RoadmapSaveResDto>> recommendRoadmapByDiagnosis(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody List<DiagnosisAnswerRequest> answers) {
    Long userId = (userDetails != null) ? userDetails.getUserId() : null;
    RoadmapSaveResDto result = diagnosisService.recommendRoadmapByDiagnosis(userId, answers);

    return ResponseEntity.ok(new Message<>(StatusCode.OK, result));
  }
}
