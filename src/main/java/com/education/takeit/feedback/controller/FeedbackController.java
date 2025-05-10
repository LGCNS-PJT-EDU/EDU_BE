package com.education.takeit.feedback.controller;

import com.education.takeit.feedback.dto.FeedbackResponseDto;
import com.education.takeit.feedback.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@Tag(name = "피드백 ", description = "피드백 관련 API")
public class FeedbackController {

  private final FeedbackService feedbackService;

  // 차후에 URI 변경 가능성 존재
  @GetMapping("/retrieve")
  @Operation(
      summary = "특정 사용자 피드백 목록 조회",
      description = "Parameter로 주어진 userId와 일치하는 사용자의 모든 피드백 데이터를 JSON 형태로 반환")
  public ResponseEntity<List<FeedbackResponseDto>> findAllFeedback(@RequestParam String userId) {
    List<FeedbackResponseDto> list = feedbackService.findFeedback(userId);
    return ResponseEntity.ok(list);
  }
}
