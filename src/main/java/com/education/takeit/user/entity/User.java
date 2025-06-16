package com.education.takeit.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 무분별하게 new User() 하는 것을 막음
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "nickname", nullable = false)
  private String nickname;

  @Column(name = "password", nullable = true)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "login_type", nullable = false)
  private LoginType loginType; // 어떤 방식으로 로그인 했는지 저장

  @Column(name = "is_active", nullable = false)
  private Boolean active = true; // 계정 활성화 여부(탈퇴 여부) 저장

  @Enumerated(EnumType.STRING)
  private LectureAmount lectureAmount;

  @Enumerated(EnumType.STRING)
  private PriceLevel priceLevel;

  @Column(name = "likes_books")
  private Boolean likesBooks; // Y: true, N: false

  @Column(name = "privacy_status")
  private Boolean privacyStatus = null;

  @Column(name = "role")
  @Enumerated(EnumType.STRING)
  private Role role;

  @Builder
  public User(String email, String nickname, String password, LoginType loginType, Role role, Boolean privacyStatus) {
    this.email = email;
    this.nickname = nickname;
    this.password = password;
    this.loginType = loginType;
    this.role = role;
    this.privacyStatus=privacyStatus;
  }

  public void changeActivateStatus() {
    if (active) {
      active = false;
    }
  }

  public void updatePreferences(
      LectureAmount lectureAmount, PriceLevel priceLevel, Boolean likesBooks) {
    this.lectureAmount = lectureAmount;
    this.priceLevel = priceLevel;
    this.likesBooks = likesBooks;
  }

  public void savePrivacyStatus() {
    privacyStatus = true;
  }
}
