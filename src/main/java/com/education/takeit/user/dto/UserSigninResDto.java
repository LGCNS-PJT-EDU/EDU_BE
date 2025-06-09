package com.education.takeit.user.dto;

public record UserSigninResDto(String accessToken, String refreshToken, Boolean privacyStatus) {
  public UserSigninResDto(String accessToken, String refreshToken) {
    this(accessToken, refreshToken, null);
  }
}
