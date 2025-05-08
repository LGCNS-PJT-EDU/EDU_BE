package com.education.takeit.feedback.controller;

import com.education.takeit.feedback.dto.FeedbackResponse;
import com.education.takeit.feedback.service.FeedbackService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

  private final FeedbackService feedbackService;

  // 차후에 URI 변경 가능성 존재
  @GetMapping("/retrieve")
  public ResponseEntity<List<FeedbackResponse>> findAllFeedback(@RequestParam String userId) {
    List<FeedbackResponse> list = feedbackService.findFeedback(userId);
    return ResponseEntity.ok(list);
  }
}
