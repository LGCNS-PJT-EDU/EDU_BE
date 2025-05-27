package com.education.takeit.oauth.controller;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.oauth.dto.OAuthLoginRequest;
import com.education.takeit.oauth.service.GoogleOAuthService;
import com.education.takeit.oauth.service.KakaoOAuthService;
import com.education.takeit.oauth.service.NaverOAuthService;
import com.education.takeit.user.dto.UserSigninResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "소셜 로그인", description = "OAuth 소셜 로그인 API")
public class OAuthController {

  private final GoogleOAuthService googleOAuthService;
  private final KakaoOAuthService kakaoOAuthService;
  private final NaverOAuthService naverOAuthService;

  /**
   * Google OAuth 소셜 로그인
   *
   * @param request
   * @return
   */
  @PostMapping("/google/login")
  @Operation(summary = "Google OAuth 소셜 로그인", description = "Google OAuth 소셜 로그인 API")
  public ResponseEntity<Message> loginWithGoogle(@RequestBody OAuthLoginRequest request) {
    UserSigninResDto userSigninResDto = googleOAuthService.login(request);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + userSigninResDto.accessToken());

    return ResponseEntity.ok().headers(headers).body(new Message(StatusCode.OK, userSigninResDto));
  }

  /**
   * Kakao OAuth 소셜 로그인
   *
   * @param request
   * @return
   */
  @PostMapping("/kakao/login")
  @Operation(summary = "Kakao OAuth 소셜 로그인", description = "Kakao OAuth 소셜 로그인 API")
  public ResponseEntity<Message> loginWithKakao(@RequestBody OAuthLoginRequest request) {
    UserSigninResDto userSigninResDto = kakaoOAuthService.login(request);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + userSigninResDto.accessToken());

    Message message = new Message(StatusCode.OK);

    return ResponseEntity.ok().headers(headers).body(new Message(StatusCode.OK, userSigninResDto));
  }

  /**
   * @param request
   * @return
   */
  @PostMapping("/naver/login")
  @Operation(summary = "Naver OAuth 소셜 로그인", description = "Naver OAuth 소셜 로그인 API")
  public ResponseEntity<Message> loginWithNaver(@RequestBody OAuthLoginRequest request) {
    UserSigninResDto userSigninResDto = naverOAuthService.login(request);

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + userSigninResDto.accessToken());

    Message message = new Message(StatusCode.OK);

    return ResponseEntity.ok().headers(headers).body(new Message(StatusCode.OK, userSigninResDto));
  }
}
