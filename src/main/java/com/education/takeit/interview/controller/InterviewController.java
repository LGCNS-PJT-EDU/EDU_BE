package com.education.takeit.interview.controller;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.interview.dto.InterviewContentResDto;
import com.education.takeit.interview.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/interview")
@Tag(name = "면접", description = "면접 질문 관련 API")
public class InterviewController {
  private final InterviewService interviewService;

  @GetMapping("/list")
  @Operation(summary = "면접 질문 조회", description = "과목별 면접 질문 랜덤하게 3문제씩 조회")
  public ResponseEntity<Message> getInterviewContent(@RequestParam("subjectId") Long subjectId) {
    List<InterviewContentResDto> interviewContentResDtoList =
        interviewService.getInterview(subjectId);
    return ResponseEntity.ok(new Message(StatusCode.OK, interviewContentResDtoList));
  }
}
