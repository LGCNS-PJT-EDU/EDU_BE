package com.education.takeit.feedback.controller;

import com.education.takeit.feedback.dto.FeedbackResponseDto;
import com.education.takeit.feedback.service.FeedbackService;
import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@Tag(name = "피드백", description = "피드백 관련 API")
public class FeedbackController {

  private final FeedbackService feedbackService;

  // 차후에 URI 변경 가능성 존재
  @GetMapping("/retrieve")
  @Operation(summary = "특정 사용자 피드백 목록 조회", description = "사용자의 모든 피드백 데이터를 JSON 형태로 반환하는 API")
  public ResponseEntity<Message<List<FeedbackResponseDto>>> findAllFeedback(
          @AuthenticationPrincipal CustomUserDetails userDetails,
          @RequestParam Long subjectId) {
    Long userId = userDetails.getUserId();
    List<FeedbackResponseDto> list = feedbackService.findFeedback(userId, subjectId);
    return ResponseEntity.ok(new Message<>(StatusCode.OK, list));
  }
}
