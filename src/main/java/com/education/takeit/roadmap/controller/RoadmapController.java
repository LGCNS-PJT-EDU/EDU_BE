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
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roadmap")
public class RoadmapController {
  private final RoadmapService roadmapService;

  @PostMapping("/create")
  @Operation(
          summary = "진단 결과로 로드맵 요청",
          description = "게스트일 경우 uuid와 함께 로드맵 반환, 로그인한 사용자일 경우 로드맵 저장 및 반환")
  public RoadmapResponseDto getRoadmap(
          @RequestHeader(value = "accessToken", required = false) String flag,
          @RequestBody List<RoadmapRequestDto> answers) {
    return roadmapService.roadmapSelect(flag, answers);
  }

  @PostMapping("/save")
  @Operation(summary = "회원가입한 사용자의 로드맵 저장 요청", description = "게스트 상태에서 로드맵받은 후 회원가입해서 로드맵을 DB에 저장")
  public void saveRoadmap(
          @RequestHeader(value = "accessToken") String accessToken,
          @RequestBody Map<String, String> body) {
    roadmapService.saveGuestRoadmap(body.get("uuid"), accessToken);
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
