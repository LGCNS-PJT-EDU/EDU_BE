package com.education.takeit.user.service;
import com.education.takeit.global.security.JwtUtils;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final List<OAuth2LoginService> oAuth2LoginServices;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public String loginByOAuth(String code, LoginType loginType) {
        // 여러 소셜 로그인 서비스 돌면서
        for (OAuth2LoginService service : oAuth2LoginServices) {
            if (service.supports().equals(loginType)) { // 로그인 타입 맞는거 찾기
                User user = service.toEntityUser(code, loginType);
                User savedUser = userRepository.findByEmail(user.getEmail())
                        .orElseGet(()-> userRepository.save(user));
                // JWT 토큰 발급
                return jwtUtils.createToken(savedUser.getId(), savedUser.getEmail());


            }
        }
        throw new IllegalArgumentException("지원하지 않는 플랫폼입니다.");
    }
}
