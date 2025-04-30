package com.education.takeit.diagnosis.controller;

import com.education.takeit.diagnosis.dto.DiagnosisResponse;
import com.education.takeit.diagnosis.service.DiagnosisService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<List<DiagnosisResponse>> getDiagnosis() {
    List<DiagnosisResponse> result = diagnosisService.getDiagnosis();
    return ResponseEntity.ok(result);
  }
}
