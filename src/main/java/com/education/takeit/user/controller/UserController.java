package com.education.takeit.user.controller;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.security.CustomUserDetails;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.user.dto.UserSigninReqDto;
import com.education.takeit.user.dto.UserSigninResDto;
import com.education.takeit.user.dto.UserSignupReqDto;
import com.education.takeit.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "사용자", description = "사용자와 관련된 API")
public class UserController {

  private final UserService userService;
  private final JwtUtils jwtUtils;

  @PostMapping("/signup")
  @Operation(summary = "회원가입", description = "자체 서비스 회원가입 API")
  public ResponseEntity<Message> signUp(@Valid @RequestBody UserSignupReqDto userSignupReqDto) {
    userService.signUp(userSignupReqDto);
    return ResponseEntity.ok(new Message(StatusCode.OK));
  }

  @PostMapping("/signin")
  @Operation(summary = "로그인", description = "자체 서비스 로그인 API")
  public ResponseEntity<Message> signIn(@RequestBody UserSigninReqDto userSigninReqDto) {
    UserSigninResDto userSigninResDto = userService.signIn(userSigninReqDto);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + userSigninResDto.accessToken());

    ResponseCookie refreshTokenCookie =
        ResponseCookie.from("refreshToken", userSigninResDto.refreshToken())
            .httpOnly(true) // JS에서 접근 불가능하게
            .secure(false) // HTTPS 통신에서만 전송
            .path("/") // 모든 경로에 대해 쿠키 유효
            .maxAge(Duration.ofDays(14)) // 만료 시간 설정
            .sameSite("Lax") // 또는 "Lax"/"None"
            .build();

    headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

    return ResponseEntity.ok()
        .headers(headers)
        .body(new Message(StatusCode.OK, "accessToken : " + userSigninResDto.accessToken()));
  }

  @DeleteMapping("/signout")
  @Operation(summary = "로그아웃", description = "로그아웃 API")
  public ResponseEntity<Message> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = userDetails.getUserId();
    userService.signOut(userId);
    return ResponseEntity.ok(new Message(StatusCode.OK));
  }

  @GetMapping("/check-email")
  @Operation(summary = "이메일 중복확인", description = "이메일 중복확인 API")
  public ResponseEntity<Message> checkEmail(@RequestParam("email") String email) {
    return ResponseEntity.ok(new Message(StatusCode.OK, userService.checkDuplicate(email)));
  }

  @PostMapping("/withdraw")
  @Operation(summary = "회원탈퇴", description = "회원 탈퇴 API")
  public ResponseEntity<Message> Withdraw(@AuthenticationPrincipal CustomUserDetails principal) {
    userService.withdraw(principal.getUserId());
    return ResponseEntity.ok(new Message(StatusCode.OK));
  }

  @PostMapping("/refresh")
  @Operation(summary = "엑세스 토큰 재발급", description = "만료된 액세스 토큰 재발급 API")
  public ResponseEntity<Message> refreshAccessToken(@RequestParam String refreshToken) {
    Long userId = jwtUtils.getUserId(refreshToken);
    if (!jwtUtils.validateRefreshToken(userId, refreshToken)) {
      return ResponseEntity.ok(new Message(StatusCode.UNAUTHORIZED));
    }
    String newAccessToken = jwtUtils.generateAccessToken(userId);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + newAccessToken);
    return ResponseEntity.ok(new Message(StatusCode.OK, "accessToken : " + newAccessToken));
  }
}
