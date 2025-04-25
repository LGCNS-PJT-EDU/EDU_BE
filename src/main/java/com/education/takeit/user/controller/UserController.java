package com.education.takeit.user.controller;


import com.education.takeit.global.dto.Message;
import com.education.takeit.global.exception.StatusCode;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/oauth/naver")
    @Operation(summary = "네이버 로그인", description = "네이버 소셜 로그인 API")
    public ResponseEntity<?> loginByNaver(@RequestParam("code") String code) {
        String token = userService.loginByOAuth(code,LoginType.NAVER);
        return ResponseEntity.ok(new Message(StatusCode.OK, token));
    }
}
