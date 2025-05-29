package com.education.takeit.user.dto;

import com.education.takeit.global.validator.password.ValidPassword;
import com.education.takeit.user.entity.LoginType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserSignupReqDto(
    @Email(message = "이메일 형식이 올바르지 않습니다.") @NotBlank(message = "이메일은 필수 항목입니다.") String email,
    @NotBlank(message = "닉네임은 필수 항목입니다.") String nickname,
    @ValidPassword String password,
    LoginType loginType) {}
