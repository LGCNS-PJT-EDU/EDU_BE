package com.education.takeit.user.controller;


import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/login/naver")
    public ResponseEntity<?> loginByNaver(@RequestParam("code") String code) {
        User user = userService.loginByOAuth(code, LoginType.NAVER);
        return ResponseEntity.ok(user);
    }
}
