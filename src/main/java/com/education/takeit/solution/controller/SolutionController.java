package com.education.takeit.solution.controller;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.security.CustomUserDetails;
import com.education.takeit.solution.dto.SolutionResDto;
import com.education.takeit.solution.service.SolutionService;
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
@RequestMapping("/api/solution")
@RequiredArgsConstructor
@Tag(name = "해설", description = "해설 관련 API")
public class SolutionController {

  private final SolutionService solutionService;

  @GetMapping
  @Operation(summary = "사용자의 해설 조회", description = "사용자가 평가 본 모든 내용의 해설을 과목별 조회")
  public ResponseEntity<Message> allSolution(
      @RequestParam("subjectId") Long subjectId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = userDetails.getUserId();
    List<SolutionResDto> solutionList = solutionService.findAllUserSolutions(userId, subjectId);
    return ResponseEntity.ok(new Message(StatusCode.OK, solutionList));
  }
}
