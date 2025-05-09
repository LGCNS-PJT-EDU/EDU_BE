package com.education.takeit.diagnosis.controller;

import com.education.takeit.diagnosis.dto.DiagnosisAnswerRequest;
import com.education.takeit.diagnosis.dto.GroupedDiagnosisResponse;
import com.education.takeit.diagnosis.service.DiagnosisService;
import com.education.takeit.roadmap.dto.RoadmapResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diagnosis")
public class DiagnosisController {

  private final DiagnosisService diagnosisService;

  /**
   * 진단 문제 GET 요청(전체)
   *
   * @return
   */
  @GetMapping
  @Operation(summary = "진단문제 리스트 요청", description = "진단문제 GET API")
  public ResponseEntity<GroupedDiagnosisResponse> findAllDiagnosis() {
    GroupedDiagnosisResponse result = diagnosisService.findAllDiagnosis();
    return ResponseEntity.ok(result);
  }

  /**
   * 진단 결과 POST 요청
   *
   * @param answers
   * @return
   */
  @PostMapping
  @Operation(summary = "진단 결과 응답", description = "진단 결과 POST API")
  public ResponseEntity<RoadmapResponseDto> recommendRoadmapByDiagnosis(
      @RequestHeader(value = "Authorization", required = false) String flag,
      @RequestBody List<DiagnosisAnswerRequest> answers) {
    String accessToken = null;
    if (flag != null && flag.startsWith("Bearer ")) {
      accessToken = flag.substring("Bearer ".length());
    }
    RoadmapResponseDto result = diagnosisService.recommendRoadmapByDiagnosis(accessToken, answers);

    return ResponseEntity.ok(result);
  }
}
