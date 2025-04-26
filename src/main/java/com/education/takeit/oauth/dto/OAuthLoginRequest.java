package com.education.takeit.oauth.dto;

import com.education.takeit.user.entity.LoginType;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthLoginRequest {
    private String code;
    private LoginType loginType;
    @Nullable
    private String state; // 네이버만 필수
}