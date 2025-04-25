package com.education.takeit.user.controller;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.exception.StatusCode;
import com.education.takeit.user.dto.ReqSigninDto;
import com.education.takeit.user.dto.ReqSignupDto;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        Map<String, String> token = userService.signIn(reqSigninDto);
        return ResponseEntity.ok(new Message(StatusCode.OK, token));
    }

    @GetMapping("/oauth/naver")
    @Operation(summary = "네이버 로그인", description = "네이버 소셜 로그인 API")
    public ResponseEntity<Message> loginByNaver(@RequestParam("code") String code) {
        String token = userService.loginByOAuth(code, LoginType.NAVER);
        return ResponseEntity.ok(new Message(StatusCode.OK, token));
    }

    @DeleteMapping("/signout")
    @Operation(summary = "로그아웃", description = "로그아웃 API")
    public ResponseEntity<Message> logout(@RequestParam("accessToken") String accessToken) {
        userService.signOut(accessToken);
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }

    @GetMapping("/check-email")
    @Operation(summary = "이메일 중복확인", description = "이메일 중복확인 API")
    public ResponseEntity<Message> checkEmail(@RequestParam("email") String email) {
        userService.checkDuplicate(email);
        return ResponseEntity.ok(new Message(StatusCode.OK));
    }
}
