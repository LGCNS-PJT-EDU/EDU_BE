package com.education.takeit.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverUserResponse {
  private String resultcode;
  private String message;

  @JsonProperty("response")
  private NaverUserInfo naverUserInfo;

  public NaverUserResponse(NaverUserInfo naverUserInfo) {
    this.naverUserInfo = naverUserInfo;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class NaverUserInfo {
    private String id;

    @JsonProperty("nickname")
    private String nickname;

    private String email;
  }
}
