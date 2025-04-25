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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final List<OAuth2LoginService> oAuth2LoginServices;


    @Override
    public String loginByOAuth(String code, LoginType loginType) {
        for (OAuth2LoginService service : oAuth2LoginServices) {
            if (service.supports().equals(loginType)) {
                User user = service.toEntityUser(code, loginType);
                User savedUser = userRepository.findByEmail(user.getEmail())
                        .orElseGet(() -> userRepository.save(user));
                return jwtUtils.createToken(savedUser.getUserId(), savedUser.getEmail());
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
    public String signIn(ReqSigninDto reqSigninDto) {
        User user = userRepository.findByEmail(reqSigninDto.email())
                .orElseThrow(() -> new CustomException(StatusCode.NOT_EXIST_USER));

        if (user.getLoginType() != LoginType.LOCAL) {
            throw new CustomException(StatusCode.NOT_SUPPORT_LOCAL_LOGIN);
        }

        if (!passwordEncoder.matches(reqSigninDto.password(), user.getPassword())) {
            throw new CustomException(StatusCode.NOT_EXIST_USER);
        }
        return jwtUtils.createToken(user.getUserId(), user.getEmail());
    }
}
