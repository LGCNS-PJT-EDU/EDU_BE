package com.education.takeit.interview.controller;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.global.security.CustomUserDetails;
import com.education.takeit.interview.dto.*;
import com.education.takeit.interview.service.InterviewService;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/interview")
@Tag(name = "면접", description = "면접 관련 API")
public class InterviewController {
  private final InterviewService interviewService;
  private final UserRepository userRepository;

  @GetMapping("/list")
  @Operation(summary = "면접 질문 조회", description = "과목별 면접 질문 랜덤하게 3문제씩 조회하는 API")
  public ResponseEntity<Message<List<InterviewContentResDto>>> getInterviewContent(
      @RequestParam List<Long> subjectIds, @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = userDetails.getUserId();
    List<InterviewContentResDto> interviewContentResDtoList =
        interviewService.getInterview(subjectIds, userId);
    return ResponseEntity.ok(new Message<>(StatusCode.OK, interviewContentResDtoList));
  }

  @PostMapping("/answers")
  @Operation(summary = "면접 응답 제출", description = "한 회차 면접 응답 제출하고 AI 피드백 받아오는 API")
  public ResponseEntity<Message<List<InterviewFeedbackResDto>>> saveReplyAndGetFeedback(
          @RequestBody InterviewAllReplyReqDto reqDto,
          @AuthenticationPrincipal CustomUserDetails userDetails
  ){
    Long userId = userDetails.getUserId();
    List<InterviewFeedbackResDto> feedbacks = interviewService.saveReplyAndRequestFeedback(userId,reqDto);
    return ResponseEntity.ok(new Message<>(StatusCode.OK, feedbacks));
  }

//  @PostMapping("/feedback")
//  @Operation(summary = "면접 피드백 ", description = "사용자 면접 응답을 받아와서 openAI에 면접 피드백 생성 요청 보내는 API")
//  public ResponseEntity<Message<InterviewFeedbackResDto>> saveReplyAndGetFeedback(
//      @RequestBody UserInterviewReplyReqDto reqDto,
//      @AuthenticationPrincipal CustomUserDetails userDetails) {
//    Long userId = userDetails.getUserId();
//    User user =
//        userRepository
//            .findByUserId(userId)
//            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
//
//    InterviewFeedbackResDto response = interviewService.saveReplyAndRequestFeedback(reqDto, user);
//    return ResponseEntity.ok(new Message<>(StatusCode.OK, response));
//  }

  @GetMapping("/history")
  @Operation(summary = "면접 내역 조회", description = "사용자 면접 기록을 회차별로 조회하는 API")
  public ResponseEntity<Message<List<InterviewHistoryResDto>>> getInterviewHistory(
      @RequestParam("userId") Long userId) {
    List<InterviewHistoryResDto> interviewHistoryList =
        interviewService.getInterviewHistory(userId);
    return ResponseEntity.ok(new Message<>(StatusCode.OK, interviewHistoryList));
  }
}
