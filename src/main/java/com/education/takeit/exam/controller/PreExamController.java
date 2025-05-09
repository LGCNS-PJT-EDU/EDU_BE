package com.education.takeit.exam.controller;

import com.education.takeit.exam.dto.ExamAnswerResDto;
import com.education.takeit.exam.dto.ExamResDto;
import com.education.takeit.exam.dto.ExamResultDto;
import com.education.takeit.exam.service.PreExamService;
import com.education.takeit.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
  public ResponseEntity<List<ExamResDto>> findPreExam(
      @RequestParam Long subjectId, @AuthenticationPrincipal CustomUserDetails userDetails) {
    // Long userId = userDetails.getUserId();
    Long userId = 1L;
    List<ExamResDto> result = preExamService.findPreExam(userId, subjectId);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/pre")
  public ResponseEntity<ExamResultDto> submitPreExamResult(
      @RequestBody ExamAnswerResDto examAnswerRes,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    // Long userId = userDetails.getUserId();
    Long userId = 1L;
    ExamResultDto result = preExamService.submitPreExam(userId, examAnswerRes);
    return ResponseEntity.ok(result);
  }
}
