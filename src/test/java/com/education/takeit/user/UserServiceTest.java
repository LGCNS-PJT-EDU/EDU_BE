package com.education.takeit.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.education.takeit.global.dto.StatusCode;
import com.education.takeit.global.exception.CustomException;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.user.dto.UserSigninReqDto;
import com.education.takeit.user.dto.UserSigninResDto;
import com.education.takeit.user.dto.UserSignupReqDto;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import com.education.takeit.user.service.UserServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtUtils jwtUtils;

  @Mock private RedisTemplate<String, Object> redisTemplate;

  @InjectMocks private UserServiceImpl userService;

  private UserSignupReqDto signupDto;
  private UserSigninReqDto signinDto;
  private User user;

  @BeforeEach
  void setUp() {
    signupDto = new UserSignupReqDto("test@example.com", "nickname", "password", LoginType.LOCAL);
    signinDto = new UserSigninReqDto("test@example.com", "password");
    user =
        User.builder()
            .email("test@example.com")
            .nickname("nickname")
            .password("encodedPassword")
            .loginType(LoginType.LOCAL)
            .build();
  }

  @Test
  void 자체_회원가입_성공() {
    // Given (테스트 조건 준비 - 가짜(mock) 객체의 동작 정의 및 입력값 준비)
    when(userRepository.existsByEmailAndLoginType(signupDto.email(), LoginType.LOCAL))
        .thenReturn(false);
    when(passwordEncoder.encode(signupDto.password())).thenReturn("encodedPassword");

    // When (실제 테스트할 대상 메소드 호출)
    assertDoesNotThrow(() -> userService.signUp(signupDto));

    // Then (기대 결과 검증 (assert, verify 등 사용)
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void 회원가입_이메일중복시_예외발생() {
    // Given
    when(userRepository.existsByEmailAndLoginType(signupDto.email(), LoginType.LOCAL))
        .thenReturn(true);

    // When
    CustomException exception =
        assertThrows(CustomException.class, () -> userService.signUp(signupDto));

    // Then
    assertEquals(StatusCode.ALREADY_EXIST_EMAIL, exception.getStatusCode());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void 자체_로그인_성공() {
    // Given
    when(userRepository.findByEmailAndLoginType(signinDto.email(), LoginType.LOCAL))
        .thenReturn(Optional.of(user));
    when(passwordEncoder.matches(signinDto.password(), user.getPassword())).thenReturn(true);

    UserSigninResDto fakeTokens = new UserSigninResDto("fake-access-token", "fake-refresh-token");

    when(jwtUtils.generateTokens(user.getRole(), user.getUserId(), user.getPrivacyStatus()))
        .thenReturn(fakeTokens);

    // When
    UserSigninResDto tokens = userService.signIn(signinDto);

    // Then
    assertThat(tokens).isEqualTo(fakeTokens);
  }

  @Test
  void 자체_로그인시_비밀번호_불일치_예외_발생() {
    // Given
    when(userRepository.findByEmailAndLoginType(signinDto.email(), LoginType.LOCAL))
        .thenReturn(Optional.of(user));
    when(passwordEncoder.matches(signinDto.password(), user.getPassword())).thenReturn(false);

    // When & Then
    assertThrows(CustomException.class, () -> userService.signIn(signinDto));
  }

  @Test
  void 이메일_중복_확인() {
    // Given
    when(userRepository.existsByEmailAndLoginType("test@example.com", LoginType.LOCAL))
        .thenReturn(true);

    // When
    boolean result = userService.checkDuplicate("test@example.com");

    // Then
    assertTrue(result);

    // Given
    when(userRepository.existsByEmailAndLoginType("new@example.com", LoginType.LOCAL))
        .thenReturn(false);

    // When & Then
    assertFalse(userService.checkDuplicate("new@example.com"));
  }

  @Test
  @DisplayName("활성화된 계정은 회원 탈퇴 시도 시 탈퇴 성공한다")
  void active가_true면_회원_탈퇴_성공() {
    Long userId = 1L;

    // When active = true
    when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));

    // No Exception
    assertDoesNotThrow(() -> userService.withdraw(userId));

    // is active = false?
    assertThat(user.getActive()).isFalse();
  }

  @Test
  @DisplayName("존재하지 않는 계정은 회원 탈퇴 시도 시 에러가 발생한다")
  void 존재하지_않는_사용자이면_회원_탈퇴_실패() {
    Long userId = 2L;

    // When user not exist
    when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

    // if user is null, return Error
    CustomException ex = assertThrows(CustomException.class, () -> userService.withdraw(userId));
    assertEquals(StatusCode.USER_NOT_FOUND, ex.getStatusCode());

    verify(userRepository, times(1)).findByUserId(userId);
  }

  @Test
  @DisplayName("로그아웃 성공")
  void 로그아웃_성공() {
    // Given
    Long userId = 99L;
    String expectedKey = userId + "'s refresh token";

    // When
    userService.signOut(userId);

    // Then
    verify(redisTemplate, times(1)).delete(expectedKey);
  }
}
