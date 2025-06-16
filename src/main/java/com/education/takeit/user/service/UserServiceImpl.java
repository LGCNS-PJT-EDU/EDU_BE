package com.education.takeit.user.service;

import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.user.dto.UserSigninReqDto;
import com.education.takeit.user.dto.UserSigninResDto;
import com.education.takeit.user.dto.UserSignupReqDto;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.Role;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtils jwtUtils;
  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void signUp(UserSignupReqDto userSignupReqDto) {
    if (userRepository.existsByEmailAndLoginType(userSignupReqDto.email(), LoginType.LOCAL)) {
      throw new CustomException(StatusCode.ALREADY_EXIST_EMAIL);
    }

    User user =
        User.builder()
            .email(userSignupReqDto.email())
            .nickname(userSignupReqDto.nickname())
            .password(passwordEncoder.encode(userSignupReqDto.password()))
            .loginType(LoginType.LOCAL)
             .privacyStatus(false)
            .role(Role.USER)
            .build();

    userRepository.save(user);
  }

  @Override
  public UserSigninResDto signIn(UserSigninReqDto userSigninReqDto) {
    User user =
        userRepository
            .findByEmailAndLoginType(userSigninReqDto.email(), LoginType.LOCAL)
            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));

    if (!passwordEncoder.matches(userSigninReqDto.password(), user.getPassword())) {
      throw new CustomException(StatusCode.INVALID_SIGNIN_INFO);
    }

    return jwtUtils.generateTokens(user.getRole(), user.getUserId(), user.getPrivacyStatus());
  }

  @Override
  public void signOut(Long userId) {

    // Redis에서 refresh token 삭제
    redisTemplate.delete(userId + "'s refresh token");
  }

  @Override
  public boolean checkDuplicate(String email) {
    return userRepository.existsByEmailAndLoginType(email, LoginType.LOCAL);
  }

  @Override
  @Transactional
  public void withdraw(Long userId) {
    User user =
        userRepository
            .findByUserId(userId)
            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));

    user.changeActivateStatus();
    // Redis에서 리프레시 토큰 삭제
    redisTemplate.delete(userId + "'s refresh token");
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
  public boolean validateRefreshToken(Long userId, String refreshToken) {
    return jwtUtils.validateRefreshToken(userId, refreshToken);
  }

  @Override
  public boolean getPrivacyStatus(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
    return user.getPrivacyStatus();
  }
}
