package com.education.takeit.oauth.dto;

import com.education.takeit.user.entity.LoginType;
import jakarta.annotation.Nullable;

public record OAuthLoginRequest(
        String code,
        LoginType loginType,
        @Nullable String state  // 네이버만 사용
) {
}