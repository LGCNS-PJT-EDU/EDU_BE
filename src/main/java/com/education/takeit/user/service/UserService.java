package com.education.takeit.user.service;

import com.education.takeit.user.dto.ReqSigninDto;
import com.education.takeit.user.dto.ReqSignupDto;

public interface UserService {

  void signUp(ReqSignupDto reqSignupDto);

  String signIn(ReqSigninDto reqSigninDto);

  void signOut(String accessToken);

  boolean checkDuplicate(String email);

  void Withdraw(Long userId);

  String reissueAccessToken(String expiredAccessToken);

  Long extractUserId(String token);

  boolean validateRefreshToken(Long userId);
}
