package com.education.takeit.admin.failLog.controller;

import com.education.takeit.admin.failLog.dto.FeedbackFailLogDto;
import com.education.takeit.admin.failLog.dto.RecomFailLogDto;
import com.education.takeit.kafka.feedback.service.FeedbackFailLogService;
import com.education.takeit.kafka.recommand.service.RecomFailLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class AdminFailLogController {

    private final FeedbackFailLogService feedbackFailLogService;
    private final RecomFailLogService recomFailLogService;

    @GetMapping("/feedback")
    public Page<FeedbackFailLogDto> getFeedbackFailLogs (
            @RequestParam(required=false) String nickname,
            @RequestParam(required=false) String email,
            @RequestParam(required=false) String errorCode,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdDt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return feedbackFailLogService.getPendingFailLogs(nickname, email, errorCode, pageable);
    }

    @GetMapping("/recommend")
    public Page<RecomFailLogDto> getRecomFailLogs (
            @RequestParam(required=false) String nickname,
            @RequestParam(required=false) String email,
            @RequestParam(required=false) String errorCode,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdDt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return recomFailLogService.getPendingFailLogs(nickname, email, errorCode, pageable);
    }



}
