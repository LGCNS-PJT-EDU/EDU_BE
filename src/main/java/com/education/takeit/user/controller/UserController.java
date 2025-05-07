package com.education.takeit.user.controller;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.security.CustomUserDetails;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.user.dto.ReqSigninDto;
import com.education.takeit.user.dto.ReqSignupDto;
import com.education.takeit.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자와 관련된 API")
public class UserController {

  private final UserService userService;
  private final JwtUtils jwtUtils;

  @PostMapping("/signup")
  @Operation(summary = "회원가입", description = "자체 서비스 회원가입 API")
  public ResponseEntity<Message> signUp(@RequestBody ReqSignupDto reqSignupDto) {
    userService.signUp(reqSignupDto);
    return ResponseEntity.ok(new Message(StatusCode.OK));
  }

  @PostMapping("/signin")
  @Operation(summary = "로그인", description = "자체 서비스 로그인 API")
  public ResponseEntity<Message> signIn(@RequestBody ReqSigninDto reqSigninDto) {
    String accessToken = userService.signIn(reqSigninDto);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);

    Message message = new Message(StatusCode.OK);

    return ResponseEntity.ok().headers(headers).body(message);
  }

  @PostMapping("/reissue")
  @Operation(summary = "엑세스 토큰 재발급", description = "만료된 액세스 토큰 재발급")
  public ResponseEntity<Message> reissue(
      @RequestHeader("Authorization") String expiredAccessToken) {
    String token = expiredAccessToken.replace("Bearer ", "").trim();

    Long userId = userService.extractUserId(token);
    if (!userService.validateRefreshToken(userId)) {
      return ResponseEntity.status(401).body(new Message(StatusCode.UNAUTHORIZED));
    }
    String newAccessToken = userService.reissueAccessToken(token);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + newAccessToken);
    Message message = new Message(StatusCode.OK);
    return ResponseEntity.ok().headers(headers).body(message);
  }

  @DeleteMapping("/signout")
  @Operation(summary = "로그아웃", description = "로그아웃 API")
  public ResponseEntity<Message> logout(
      @RequestHeader("Authorization") String authorizationHeader) {
    String accessToken = authorizationHeader.replace("Bearer ", "");
    userService.signOut(accessToken);
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
}
