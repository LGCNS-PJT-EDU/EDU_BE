package com.education.takeit.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NaverUserResponse {
    private String resultcode;
    private String message;

    @JsonProperty("response")
    private NaverUserInfo naverUserInfo;

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
