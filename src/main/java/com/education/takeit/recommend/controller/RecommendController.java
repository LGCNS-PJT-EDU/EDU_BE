package com.education.takeit.recommend.controller;

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
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/recommend")
@Tag(name = "추천 컨텐츠", description = "추천 컨텐츠 관련 API")
public class RecommendController {

  private final RecommendService recommendService;

  @GetMapping
  @Operation(summary = "추천받은 컨텐츠 조회", description = "사용자가 추천받은 컨텐츠 조회하는 API")
  public ResponseEntity<List<UserContentResDto>> getUserContent(
      @RequestParam("userId") Long userId) {
    List<UserContentResDto> contentList = recommendService.getUserContent(userId);
    return ResponseEntity.ok(contentList);
  }

  @GetMapping
  public Mono<ResponseEntity<List<UserContentResDto>>> getRecommendation(@RequestParam Long userId, @RequestParam Long subjectId) {
    return recommendService.fetchAndSaveRecommendation(userId,subjectId)
            .map(ResponseEntity::ok);
  }
}
