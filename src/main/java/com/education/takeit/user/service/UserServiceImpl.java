package com.education.takeit.user.service;

import com.education.takeit.global.exception.CustomException;
import com.education.takeit.global.exception.StatusCode;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.user.dto.ReqSigninDto;
import com.education.takeit.user.dto.ReqSignupDto;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final List<OAuth2LoginService> oAuth2LoginServices;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String loginByOAuth(String code, LoginType loginType) {
        for (OAuth2LoginService service : oAuth2LoginServices) {
            if (service.supports().equals(loginType)) {
                User user = service.toEntityUser(code, loginType);
                User savedUser = userRepository.findByEmail(user.getEmail())
                        .orElseGet(() -> userRepository.save(user));
                return jwtUtils.generateAccessToken(savedUser.getUserId());
            }
        }
        throw new IllegalArgumentException("지원하지 않는 플랫폼입니다.");
    }


    @Override
    public void signUp(ReqSignupDto reqSignupDto) {
        if (userRepository.existsByEmail(reqSignupDto.email())) {
            throw new CustomException(StatusCode.ALREADY_EXIST_EMAIL);
        }

        User user = User.builder()
                .email(reqSignupDto.email())
                .nickname(reqSignupDto.nickname())
                .password(passwordEncoder.encode(reqSignupDto.password()))
                .loginType(LoginType.LOCAL)
                .build();

        userRepository.save(user);
    }

    
    @Override
    public Map<String, String> signIn(ReqSigninDto reqSigninDto) {
        User user = userRepository.findByEmail(reqSigninDto.email())
                .orElseThrow(() -> new CustomException(StatusCode.NOT_EXIST_USER));

        if (user.getLoginType() != LoginType.LOCAL) {
            throw new CustomException(StatusCode.NOT_SUPPORT_LOCAL_LOGIN);
        }

        if (!passwordEncoder.matches(reqSigninDto.password(), user.getPassword())) {
            throw new CustomException(StatusCode.NOT_EXIST_USER);
        }
        return jwtUtils.generateTokens(user.getUserId());
    }

    @Override
    public void signOut(String accessToken) {
        // 1. access token에서 userId 추출
        Long userId = jwtUtils.getUserId(accessToken);

        // 2. Redis에서 refresh token 삭제
        redisTemplate.delete(userId + "'s refresh token");
    }


    @Override
    public boolean checkDuplicate(String email) {
        return userRepository.existsByEmailAndLoginType(email, LoginType.LOCAL);
    }
}
