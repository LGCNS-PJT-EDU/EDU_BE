package com.education.takeit.roadmap.controller;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.security.CustomUserDetails;
import com.education.takeit.roadmap.dto.RoadmapFindResDto;
import com.education.takeit.roadmap.dto.SubjectDto;
import com.education.takeit.roadmap.dto.SubjectFindResDto;
import com.education.takeit.roadmap.service.RoadmapService;
import com.education.takeit.roadmap.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roadmap")
@Tag(name = "로드맵", description = "로드맵 관련 API")
public class RoadmapController {
  private final RoadmapService roadmapService;
  private final SubjectService subjectService;

  /*  @PostMapping("/create")
  @Operation(
      summary = "진단 결과로 로드맵 요청",
      description = "게스트일 경우 uuid와 함께 로드맵 반환, 로그인한 사용자일 경우 로드맵 저장 및 반환")
  public RoadmapResponseDto getRoadmap(
      @RequestHeader(value = "accessToken", required = false) String flag,
      @RequestBody List<DiagnosisAnswerRequest> answers) {
    return roadmapService.roadmapSelect(flag, answers);
  }*/

  @PostMapping("/save")
  @Operation(summary = "회원가입한 사용자의 로드맵 저장 요청", description = "게스트 상태에서 로드맵받은 후 회원가입해서 로드맵을 DB에 저장")
  public ResponseEntity<Message> saveRoadmap(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody Map<String, String> body) {
    System.out.println(userDetails);
    Long userId = userDetails.getUserId();
    roadmapService.saveGuestRoadmap(body.get("uuid"), userId);
    return ResponseEntity.ok(new Message(StatusCode.OK));
  }

  @GetMapping("/users/{userId}/progress")
  @Operation(summary = "마이페이지에서 로드맵 진척도 조회", description = "전체 과목 중 이수한 과목 수 백분율로 계산해서 반환")
  public ResponseEntity<Integer> getUserProgress(@PathVariable Long userId) {
    int percentage = roadmapService.getProgressPercentage(userId);
    return ResponseEntity.ok(percentage);
  }

  @PutMapping
  @Operation(summary = "로드맵 수정", description = "사용자가 원하는 대로 로드맵 과목 수정")
  public ResponseEntity<Message> updateRoadmap(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody List<SubjectDto> subjects) {
    Long userId = userDetails.getUserId();
    roadmapService.updateRoadmap(userId, subjects);

    return ResponseEntity.ok(new Message(StatusCode.OK));
  }

  @DeleteMapping
  @Operation(summary = "로드맵 삭제", description = "로드맵 삭제")
  public ResponseEntity<Message> deleteRoadmap(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    {
      Long userId = userDetails.getUserId();
      roadmapService.deleteRoadmap(userId);
      return ResponseEntity.ok(new Message(StatusCode.OK));
    }
  }

  @GetMapping("/default")
  @Operation(summary = "기본 로드맵 제공", description = "기본 로드맵을 반환")
  public ResponseEntity<List<SubjectDto>> findDefaultRoadmap(
      @RequestParam("roadmap") String defaultRoadmapType) {
    List<SubjectDto> defaultRoadmap = roadmapService.getDefaultRoadmap(defaultRoadmapType);
    return ResponseEntity.ok(defaultRoadmap);
  }

  @PostMapping("/default")
  @Operation(summary = "기본 로드맵을 사용자에게 저장", description = "기본 로드맵을 사용자에게 할당")
  public ResponseEntity<Message> saveDefaultRoadmap(
      @RequestParam("roadmap") String defaultRoadmapType,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = userDetails.getUserId();
    roadmapService.saveDefaultRoadmap(defaultRoadmapType, userId);
    return ResponseEntity.ok(new Message(StatusCode.OK));
  }

  @GetMapping
  @Operation(summary = "사용자 로드맵 제공", description = "사용자 로드맵을 제공")
  public ResponseEntity<RoadmapFindResDto> findUserRoadmap(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = userDetails.getUserId();
    RoadmapFindResDto userRoadmap = roadmapService.findUserRoadmap(userId);
    return ResponseEntity.ok(userRoadmap);
  }

  @GetMapping("/subject")
  @Operation(summary = "사용자 과목 정보 제공", description = "사용자가 과목을 눌렀을 때 필요한 정보 제공")
  public ResponseEntity<SubjectFindResDto> findUserRoadmap(
          @AuthenticationPrincipal CustomUserDetails userDetails,
          @RequestParam("subjectId") Long subjectId){
    Long userId = userDetails.getUserId();
    SubjectFindResDto subjectFindResDto =  subjectService.findUserSubject(userId, subjectId);
    return ResponseEntity.ok(subjectFindResDto);
  }
}
