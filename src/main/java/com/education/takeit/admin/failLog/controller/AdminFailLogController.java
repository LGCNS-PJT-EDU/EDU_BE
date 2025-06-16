package com.education.takeit.admin.failLog.controller;

import com.education.takeit.admin.failLog.dto.DailyLogCountDto;
import com.education.takeit.admin.failLog.dto.FeedbackFailLogDto;
import com.education.takeit.admin.failLog.dto.RecomFailLogDto;
import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.kafka.feedback.service.FeedbackFailLogService;
import com.education.takeit.kafka.recommand.service.RecomFailLogService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/fail-logs")
@RequiredArgsConstructor
public class AdminFailLogController {

  private final FeedbackFailLogService feedbackFailLogService;
  private final RecomFailLogService recomFailLogService;

  /**
   * 피드백 실패 로그 조회 (페이징 + 검색)
   *
   * @param nickname
   * @param email
   * @param errorCode
   * @param pageable
   * @return
   */
  @GetMapping("/feedback")
  public ResponseEntity<Message<Page<FeedbackFailLogDto>>> getFeedbackFailLogs(
      @RequestParam(required = false) String nickname,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String errorCode,
      @PageableDefault(page = 0, size = 10, sort = "createdDt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    Page<FeedbackFailLogDto> paged =
        feedbackFailLogService.getPendingFailLogs(nickname, email, errorCode, pageable);
    return ResponseEntity.ok(new Message<>(StatusCode.OK, paged));
  }

  /**
   * 추천 실패 로그 조회 (페이징 + 검색)
   *
   * @param nickname
   * @param email
   * @param errorCode
   * @param pageable
   * @return
   */
  @GetMapping("/recommend")
  public ResponseEntity<Message<Page<RecomFailLogDto>>> getRecomFailLogs(
      @RequestParam(required = false) String nickname,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String errorCode,
      @PageableDefault(page = 0, size = 10, sort = "createdDt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    Page<RecomFailLogDto> paged =
        recomFailLogService.getPendingFailLogs(nickname, email, errorCode, pageable);
    return ResponseEntity.ok(new Message<>(StatusCode.OK, paged));
  }

  /**
   * 피드백 실패 로그 일자별 건수 조회
   *
   * @return
   */
  @GetMapping("/feedback/daily-counts")
  public ResponseEntity<Message<List<DailyLogCountDto>>> getFeedbackDailyCounts() {
    List<DailyLogCountDto> list = feedbackFailLogService.getDailyCounts();
    return ResponseEntity.ok(new Message<>(StatusCode.OK, list));
  }

  /**
   * 추천 실패 로그 일자별 건수 조회
   *
   * @return
   */
  @GetMapping("/recommend/daily-counts")
  public ResponseEntity<Message<List<DailyLogCountDto>>> getRecommendDailyCounts() {
    List<DailyLogCountDto> list = recomFailLogService.getDailyCounts();
    return ResponseEntity.ok(new Message<>(StatusCode.OK, list));
  }

  /**
   * 피드백 실패 로그 재시도
   *
   * @param id
   * @return
   */
  @PostMapping("/feedback/{id}/retry")
  public ResponseEntity<Message<Void>> retryFeedbackLog(@PathVariable Long id) {
    feedbackFailLogService.retryFailLog(id);
    return ResponseEntity.ok(new Message<>(StatusCode.OK, null));
  }

  /**
   * 추천 실패 로그 재시도
   *
   * @param id
   * @return
   */
  @PostMapping("/recommend/{id}/retry")
  public ResponseEntity<Message<Void>> retryRecommendLog(@PathVariable Long id) {
    recomFailLogService.retryFailLog(id);
    return ResponseEntity.ok(new Message<>(StatusCode.OK, null));
  }
}
