package com.education.takeit.user.service;

import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.user.dto.ReqSigninDto;
import com.education.takeit.user.dto.ReqSignupDto;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, String> redisTemplate;


    @Override
    public void signUp(ReqSignupDto reqSignupDto) {
        if (userRepository.existsByEmailAndLoginType(reqSignupDto.email(), LoginType.LOCAL)) {
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
    public String signIn(ReqSigninDto reqSigninDto) {
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
        // access token에서 userId 추출
        Long userId = jwtUtils.getUserId(accessToken);

        // Redis에서 refresh token 삭제
        redisTemplate.delete(userId + "'s refresh token");
    }


    @Override
    public boolean checkDuplicate(String email) {
        return userRepository.existsByEmailAndLoginType(email, LoginType.LOCAL);
    }


    @Override
    @Transactional
    public void Withdraw(Long userId) {
        User user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new CustomException(StatusCode.NOT_EXIST_USER);
        }

        user.changeActivateStatus();
    }
    @Override
    public String reissueAccessToken(String expiredAccessToken) {
        Long userId = jwtUtils.getUserId(expiredAccessToken);

        String storedRefreshToken = redisTemplate.opsForValue().get("refresh:" + userId);
        if (storedRefreshToken == null) {
            throw new CustomException(StatusCode.UNAUTHORIZED);
        }

        // 새로운 엑세스토큰 발급
        return jwtUtils.generateAccessToken(userId);
    }
    @Override
    public Long extractUserId(String token) {
        try {
            return jwtUtils.getUserId(token);
        } catch (ExpiredJwtException e) {
            return Long.parseLong(e.getClaims().getSubject());
        }
    }
    @Override
    public boolean validateRefreshToken(Long userId) {
        return jwtUtils.validateRefreshToken(userId);
    }




}
