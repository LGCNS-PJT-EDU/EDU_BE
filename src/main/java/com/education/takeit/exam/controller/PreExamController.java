package com.education.takeit.exam.controller;

import com.education.takeit.exam.dto.ExamResponse;
import com.education.takeit.exam.service.PreExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
@Tag(name = "PreExam", description = "사전 평가와 관련된 API")
public class PreExamController {

  private final PreExamService preExamService;

  /**
   * 사전 평가 GET 요청(10문제)
   *
   * @return
   */
  @GetMapping("/pre")
  @Operation(summary = "사전 평가 문제 리스트 요청", description = "사전평가 문제 GET API")
  public ResponseEntity<List<ExamResponse>> getPreExam() {
    List<ExamResponse> result = preExamService.getPreExam();
    return ResponseEntity.ok(result);
  }
}
