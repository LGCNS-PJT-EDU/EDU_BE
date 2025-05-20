package com.education.takeit.exam.controller;

import com.education.takeit.exam.dto.ExamAnswerResDto;
import com.education.takeit.exam.dto.ExamResDto;
import com.education.takeit.exam.service.ExamService;
import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
@Tag(name = "평가", description = "사전 평가와 관련된 API")
public class ExamController {

  private final ExamService examService;

  /**
   * 사전 평가 GET 요청(10문제)
   *
   * @param userDetails
   * @param subjectId
   * @return
   */
  @GetMapping("/pre")
  @Operation(summary = "사전 평가 문제 리스트 요청", description = "사전평가 문제 GET API")
  public ResponseEntity<List<ExamResDto>> findPreExam(
      @AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam Long subjectId) {
    Long userId = userDetails.getUserId();
    List<ExamResDto> result = examService.findPreExam(userId, subjectId);
    return ResponseEntity.ok(result);
  }

  /**
   * 사전 평가 POST
   *
   * @param userDetails
   * @param examAnswerRes
   * @return
   */
  @PostMapping("/pre")
  @Operation(summary = "사전 평가 문제 결과 응답 및 전달", description = "사전평가 결과 POST API")
  public ResponseEntity<Message> submitPreExamResult(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody ExamAnswerResDto examAnswerRes) {
    Long userId = userDetails.getUserId();
    examService.submitPreExam(userId, examAnswerRes);
    return ResponseEntity.ok(new Message(StatusCode.OK));
  }

  /**
   * 사후 평가 GET 요청(15문제)
   *
   * @param userDetails
   * @param subjectId
   * @return
   */
  @GetMapping("/post")
  @Operation(summary = "사후 평가 문제 리스트 요청", description = "사후평가 문제 GET API")
  public ResponseEntity<List<ExamResDto>> findPostExam(
      @AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam Long subjectId) {
    Long userId = userDetails.getUserId();
    List<ExamResDto> result = examService.findPostExam(userId, subjectId);
    return ResponseEntity.ok(result);
  }

  /**
   * 사후 평가 POST
   *
   * @param userDetails
   * @param examAnswerRes
   * @return
   */
  @PostMapping("/post")
  @Operation(summary = "사후 평가 문제 결과 응답 및 전달", description = "사후평가 결과 POST API")
  public ResponseEntity<Message> submitPostExamResult(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody ExamAnswerResDto examAnswerRes) {
    Long userId = userDetails.getUserId();
    examService.submitPostExam(userId, examAnswerRes);
    return ResponseEntity.ok(new Message(StatusCode.OK));
  }
}
