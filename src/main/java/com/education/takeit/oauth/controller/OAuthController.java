package com.education.takeit.oauth.controller;

import com.education.takeit.global.dto.Message;
import com.education.takeit.global.exception.StatusCode;
import com.education.takeit.oauth.dto.OAuthLoginRequest;
import com.education.takeit.oauth.service.GoogleOAuthService;
import com.education.takeit.oauth.service.KakaoOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class OAuthController {

    private GoogleOAuthService googleOAuthService;
    private KakaoOAuthService kakaoOAuthService;

    @PostMapping("/google/login")
    public ResponseEntity<Message> loginWithGoogle(@RequestBody OAuthLoginRequest request) {
        String token = googleOAuthService.login(request);
        return ResponseEntity.ok(new Message(StatusCode.OK, token));
    }

    @PostMapping("/login")
    public ResponseEntity<Message> loginWithKakao(@RequestBody OAuthLoginRequest request) {
        String token = kakaoOAuthService.login(request);
        return ResponseEntity.ok(new Message(StatusCode.OK, token));
    }
}
