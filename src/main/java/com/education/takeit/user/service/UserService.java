package com.education.takeit.user.service;

import com.education.takeit.user.dto.ReqSigninDto;
import com.education.takeit.user.dto.ReqSignupDto;
import java.util.Map;

public interface UserService {
  void signUp(ReqSignupDto reqSignupDto);

  Map<String, String> signIn(ReqSigninDto reqSigninDto);

  void signOut(String accessToken);

  boolean checkDuplicate(String email);

  void Withdraw(Long userId);
}
