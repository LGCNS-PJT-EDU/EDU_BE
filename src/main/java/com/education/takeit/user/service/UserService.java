package com.education.takeit.user.service;


import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.dto.ReqSigninDto;
import com.education.takeit.user.dto.ReqSignupDto;

public interface UserService {
    void signUp(ReqSignupDto reqSignupDto);
    String signIn(ReqSigninDto reqSigninDto);
  String loginByOAuth(String code, LoginType loginType);
}
