package com.education.takeit.oauth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.global.exception.StatusCode;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.oauth.client.GoogleOauthClient;
import com.education.takeit.oauth.dto.OAuthLoginRequest;
import com.education.takeit.oauth.dto.OAuthTokenResponse;
import com.education.takeit.oauth.property.GoogleProperties;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService implements OAuthService{

    private final GoogleOauthClient googleClient;
    private final UserRepository userRepository;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final JwtUtils jwtUtils;

    @Override
    public String login(OAuthLoginRequest request) {
        OAuthTokenResponse token = googleClient.getToken(request);

        Map<String, String> userInfo = validateIdToken(token);
        LoginType loginType = request.getLoginType();

        User user = userRepository.findByEmailAndLoginType(userInfo.get("email"), loginType)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(userInfo.get("email"))
                        .nickname(userInfo.get("nickname"))
                        .loginType(loginType)
                        .build()));

        return jwtUtils.createToken(user.getUserId(), user.getEmail());
    }

    @Override
    public Map<String, String> validateIdToken(OAuthTokenResponse token) {
        GoogleIdToken idToken;
        try {
            idToken = googleIdTokenVerifier.verify(token.getIdToken());
        } catch (Exception e) {
            throw new CustomException(StatusCode.INVALID_GOOGLE_ID_TOKEN);
        }
        if (idToken == null) {
            throw new CustomException(StatusCode.INVALID_GOOGLE_ID_TOKEN);
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String nickname = (String) payload.get("name");

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("email", email);
        userInfo.put("nickname", nickname);

        return userInfo;
    }
}
