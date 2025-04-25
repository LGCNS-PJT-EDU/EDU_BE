package com.education.takeit.user.dto;

import com.education.takeit.user.entity.LoginType;

public record ReqSignupDto(
        String email,
        String nickname,
        String password,
        LoginType loginType
) {}
