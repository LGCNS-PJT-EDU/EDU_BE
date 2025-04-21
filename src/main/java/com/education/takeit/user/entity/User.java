package com.education.takeit.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 무분별하게 new User() 하는 것을 막음
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // pk Long 타입의 id

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId; // 로그인할 때 입력하는 회원의 id

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type",nullable = false)
    private LoginType loginType; // 어떤 방식으로 로그인 했는지 저장

    @Builder
    public User(String userId, String email, String nickname, String password, LoginType loginType) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.loginType = loginType;
    }
}
