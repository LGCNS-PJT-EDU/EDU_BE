//package com.education.takeit.oauth.controller;
//
//import com.education.takeit.global.dto.Message;
//import com.education.takeit.global.exception.StatusCode;
//import com.education.takeit.oauth.dto.KakaoLoginRequest;
//import com.education.takeit.oauth.service.GoogleOAuthService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/auth/google")
//@RequiredArgsConstructor
//public class GoogleAuthController {
//
//    private final GoogleOAuthService googleOAuthService;
//
//    @PostMapping("/login")
//    public ResponseEntity<Message> login(@RequestBody KakaoLoginRequest request) {
//        String token = googleOAuthService.loginWithGoogle(request);
//        return ResponseEntity.ok(new Message(StatusCode.OK, token));
//    }
//}
