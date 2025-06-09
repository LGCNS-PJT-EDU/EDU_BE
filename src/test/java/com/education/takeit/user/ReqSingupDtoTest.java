package com.education.takeit.user;

import com.education.takeit.user.dto.UserSignupReqDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ReqSingupDtoTest {

  private Validator validator;

  @BeforeEach
  void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("회원가입 정상")
  void signup_success() {
    // given
    UserSignupReqDto dto = new UserSignupReqDto("test@test.com", "nickname", "abc123!@", null);
    // when
    Set<ConstraintViolation<UserSignupReqDto>> violations = validator.validate(dto);

    // then
    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("회원가입 실패_비밀번호가 8자 미만이면 실패")
  void 자체_회원가입시_비밀번호_Validation_에러_발생() {
    // given
    UserSignupReqDto dto =
        new UserSignupReqDto(
            "test@test.com",
            "nickname",
            "a1!", // 3자, 조합은 OK지만 길이 실패
            null);
    // when
    Set<ConstraintViolation<UserSignupReqDto>> violations = validator.validate(dto);

    // then
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
  }

  @Test
  @DisplayName("회원가입 실패_비밀번호 3가지 문자 미만 포함 실패")
  void 자체_회원가입시_비밀번호가_3가지_문자_미만일_때_에러_발생() {
    // given
    UserSignupReqDto dto =
        new UserSignupReqDto(
            "test@test.com",
            "nickname",
            "12345678", // 숫자만, 길이는 OK
            null);
    // when
    Set<ConstraintViolation<UserSignupReqDto>> violations = validator.validate(dto);

    // then
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
  }
}
