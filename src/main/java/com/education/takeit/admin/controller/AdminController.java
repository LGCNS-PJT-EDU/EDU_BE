package com.education.takeit.admin.controller;

import com.education.takeit.admin.dto.AdminSubjectResDto;
import com.education.takeit.admin.dto.TotalUserFindResDto;
import com.education.takeit.admin.service.AdminService;
import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "관리자", description = "관리자 관련 API")
public class AdminController {
  private final AdminService adminService;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/users")
  public ResponseEntity<Message<Page<TotalUserFindResDto>>> getUsers(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    Page<TotalUserFindResDto> pagedUsers = adminService.getPagedUsers(page, size);
    return ResponseEntity.ok(new Message<>(StatusCode.OK, pagedUsers));
  }

  @GetMapping("/subjects")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Message<Page<AdminSubjectResDto>>> getSubjects(
          @RequestParam(required = false) String keyword,
          @RequestParam(defaultValue = "id") String sortBy,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size
  ) {
    Page<AdminSubjectResDto> subjects = adminService.getSubjects(keyword, sortBy, page, size);
    return ResponseEntity.ok(new Message<>(StatusCode.OK, subjects));
  }


}
