package com.education.takeit.diagnosis.controller;

import com.education.takeit.diagnosis.dto.DiagnosisAnswerRequest;
import com.education.takeit.diagnosis.dto.GroupedDiagnosisResponse;
import com.education.takeit.diagnosis.service.DiagnosisService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diagnosis")
public class DiagnosisController {

  private final DiagnosisService diagnosisService;

  private final TestController testController;

  /**
   * 진단 문제 GET 요청(전체)
   *
   * @return
   */
  @GetMapping
  @Operation(summary = "진단문제 리스트 요청", description = "진단문제 GET API")
  public ResponseEntity<GroupedDiagnosisResponse> getDiagnosis() {
    GroupedDiagnosisResponse result = diagnosisService.getDiagnosis();
    return ResponseEntity.ok(result);
  }

  /**
   * 진단 결과 POST 요청
   * @param answers
   * @return
   */
  @PostMapping
  @Operation(summary = "진단 결과 응답", description = "진단 결과 POST API")
  public ResponseEntity<?> postDiagnosis(@RequestBody List<DiagnosisAnswerRequest> answers) {

    String result = testController.recommendByDiagnosis(answers);

    return ResponseEntity.ok(result);
  }
}
