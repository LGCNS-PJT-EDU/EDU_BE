package com.education.takeit.user.dto;

public record ReqSignupDto(
        String userId,
        String email,
        String nickname,
        String password
) {}
