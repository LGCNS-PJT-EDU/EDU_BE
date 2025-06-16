package com.education.takeit.user.service;

import com.education.takeit.user.dto.UserSigninReqDto;
import com.education.takeit.user.dto.UserSigninResDto;
import com.education.takeit.user.dto.UserSignupReqDto;

public interface UserService {

  void signUp(UserSignupReqDto userSignupReqDto);

  UserSigninResDto signIn(UserSigninReqDto userSigninReqDto);

  void signOut(Long userId);

  boolean checkDuplicate(String email);

  void withdraw(Long userId);

  String reissueAccessToken(String expiredAccessToken);

  Long extractUserId(String token);

  boolean validateRefreshToken(Long userId, String refreshToken);

  boolean getPrivacyStatus(Long userId);
}
