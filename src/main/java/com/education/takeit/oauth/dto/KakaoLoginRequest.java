package com.education.takeit.oauth.dto;

import com.education.takeit.user.entity.LoginType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoLoginRequest {
	private String code;
	private LoginType loginType;
}
