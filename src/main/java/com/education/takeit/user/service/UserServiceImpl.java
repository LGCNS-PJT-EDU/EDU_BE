package com.education.takeit.user.service;

import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final List<OAuth2LoginService> oAuth2LoginServices;


    @Override
    public String loginByOAuth(String code, LoginType loginType) {
        for (OAuth2LoginService service : oAuth2LoginServices) {
            if (service.supports().equals(loginType)) {
                User user = service.toEntityUser(code, loginType);
                User savedUser = userRepository.findByEmail(user.getEmail())
                        .orElseGet(() -> userRepository.save(user));
                return jwtUtils.createToken(savedUser.getId(), savedUser.getEmail());
            }
        }
        throw new IllegalArgumentException("지원하지 않는 플랫폼입니다.");
    }
}



