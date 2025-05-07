package com.education.takeit.roadmap.controller;

import com.education.takeit.roadmap.dto.RoadmapRequestDto;
import com.education.takeit.roadmap.dto.RoadmapResponseDto;
import com.education.takeit.roadmap.service.RoadmapService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpHeaders;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roadmap")
public class RoadmapController {
    private final RoadmapService roadmapService;

    @PostMapping("/guest")
    @Operation(summary = "게스트가 진단 결과로 로드맵 요청", description = "로드맵 및 UUID 반환 POST API")
    public RoadmapResponseDto getRoadmap(@RequestHeader(value = "accessToken", required = false) String flag, @RequestBody List<RoadmapRequestDto> answers) {
        return roadmapService.roadmapSelect(flag, answers);
    }
}
