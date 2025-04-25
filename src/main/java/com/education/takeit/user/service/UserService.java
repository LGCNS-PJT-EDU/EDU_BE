package com.education.takeit.user.service;

import com.education.takeit.user.entity.LoginType;

public interface UserService {
    String loginByOAuth(String code, LoginType loginType);
}
