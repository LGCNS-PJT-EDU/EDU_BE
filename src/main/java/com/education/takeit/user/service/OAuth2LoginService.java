package com.education.takeit.user.service;

import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;

public interface OAuth2LoginService {
    LoginType supports();
    User toEntityUser(String code, LoginType loginType);
}
