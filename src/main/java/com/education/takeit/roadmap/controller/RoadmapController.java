package com.education.takeit.roadmap.controller;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.security.CustomUserDetails;
import com.education.takeit.roadmap.dto.RoadmapRequestDto;
import com.education.takeit.roadmap.dto.RoadmapResponseDto;
import com.education.takeit.roadmap.dto.SubjectDto;
import com.education.takeit.roadmap.service.RoadmapService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roadmap")
public class RoadmapController {
  private final RoadmapService roadmapService;

  @PostMapping("/guest")
  @Operation(summary = "게스트가 진단 결과로 로드맵 요청", description = "로드맵 및 UUID 반환 POST API")
  public RoadmapResponseDto getRoadmap(@RequestBody List<RoadmapRequestDto> answers) {
    return roadmapService.getRoadmap(answers);
  }

  @GetMapping("/users/{userId}/progress")
  @Operation(summary = "마이페이지에서 로드맵 진척도 조회", description = "전체 과목 중 이수한 과목 수 백분율로 계산해서 반환")
  public ResponseEntity<Integer> getUserProgress(@PathVariable Long userId) {
    int percentage = roadmapService.getProgressPercentage(userId);
    return ResponseEntity.ok(percentage);
  }

  @PutMapping
  @Operation(summary = "로드맵 수정", description = "사용자가 원하는 대로 로드맵 과목 수정")
  public ResponseEntity<Message> updateRoadmap(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @RequestBody List<SubjectDto> subjects){
    Long userId= userDetails.getUserId();
    roadmapService.updateRoadmap(userId, subjects);

    return ResponseEntity.ok(new Message(StatusCode.OK));
  }

  @DeleteMapping
  @Operation(summary = "로드맵 삭제", description = "로드맵 삭제")
  public ResponseEntity<Message> deleteRoadmap(@AuthenticationPrincipal CustomUserDetails userDetails) {{
  Long userId= userDetails.getUserId();
  roadmapService.deleteRoadmap(userId);
  return ResponseEntity.ok(new Message(StatusCode.OK));

  }
  }
}
