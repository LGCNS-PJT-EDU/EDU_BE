package com.education.takeit.recommend.controller;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.recommend.dto.UserContentResDto;
import com.education.takeit.recommend.service.RecommendService;
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
@RequestMapping("api/recommend")
@Tag(name = "추천 컨텐츠", description = "추천 컨텐츠 관련 API")
public class RecommendController {

  private final RecommendService recommendService;

  @GetMapping("/list")
  @Operation(summary = "추천받은 컨텐츠 조회", description = "사용자가 추천받은 컨텐츠 조회하는 API")
  public ResponseEntity<Message> getUserContent(@RequestParam("userId") Long userId) {
    List<UserContentResDto> contentList = recommendService.getUserContent(userId);
    return ResponseEntity.ok(new Message(StatusCode.OK, contentList));
  }

  //  @GetMapping("/contents")
  //  @Operation(summary = "추천 컨텐츠 생성 요청", description = "fastAPI에 추천 컨텐츠 생성 요청 보내는 API")
  //  public ResponseEntity<Message> getrecommendation(
  //          @RequestParam Long userId,
  //          @RequestParam Long subjectId
  //  ){
  //    List<UserContentResDto> recommendationList =
  // recommendService.fetchAndSaveRecommendation(userId, subjectId);
  //    return ResponseEntity.ok(new Message(StatusCode.OK, recommendationList));
  //  }

}
