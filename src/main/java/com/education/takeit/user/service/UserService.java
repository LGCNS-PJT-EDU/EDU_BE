package com.education.takeit.user.service;

import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final List<OAuth2LoginService> oAuth2LoginServices;
    private final UserRepository userRepository;

    public User loginByOAuth(String code, LoginType loginType) {
        for (OAuth2LoginService service : oAuth2LoginServices) {
            if (service.supports().equals(loginType)) {
                User user = service.toEntityUser(code, loginType);

                // 기존 유저 존재 확인
                return userRepository.findByEmail(user.getEmail())
                        .orElseGet(() -> userRepository.save(user));
            }
        }
        throw new IllegalArgumentException("지원하지 않는 플랫폼입니다.");
    }
}
