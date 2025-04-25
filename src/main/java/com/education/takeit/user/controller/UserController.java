package com.education.takeit.user.controller;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.exception.StatusCode;
import com.education.takeit.user.dto.ReqSigninDto;
import com.education.takeit.user.dto.ReqSignupDto;
import com.education.takeit.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자와 관련된 API")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "자체 서비스 회원가입 API")
    public ResponseEntity<Message> signUp(@RequestBody ReqSignupDto reqSignupDto) {
        userService.signUp(reqSignupDto);
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }

    @PostMapping("/signin")
    @Operation(summary = "로그인", description = "자체 서비스 로그인 API")
    public ResponseEntity<Message> signIn(@RequestBody ReqSigninDto reqSigninDto) {
        String token = userService.signIn(reqSigninDto);
        return ResponseEntity.ok(new Message(StatusCode.OK, token));
    }

    @GetMapping("/check-email")
    @Operation(summary = "이메일 중복확인", description = "자체 회원가입에서 중복된 이메일이 있는지 확인하는 API")
    public ResponseEntity<Message> checkEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(new Message(StatusCode.OK, userService.checkDuplicate(email)));
    }

}
