package com.education.takeit.user.service;

import com.education.takeit.global.exception.CustomException;
import com.education.takeit.global.exception.StatusCode;
import com.education.takeit.user.dto.ReqSignupDto;
import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import com.education.takeit.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void signUp(ReqSignupDto reqSignupDto) {
        if (userRepository.existsByUserId(reqSignupDto.userId())) {
            throw new CustomException(StatusCode.ALREADY_EXIST_USERID);
        }
        if (userRepository.existsByEmail(reqSignupDto.email())) {
            throw new CustomException(StatusCode.ALREADY_EXIST_EMAIL);
        }

        User user = User.builder()
                .userId(reqSignupDto.userId())
                .email(reqSignupDto.email())
                .nickname(reqSignupDto.nickname())
                .password(passwordEncoder.encode(reqSignupDto.password()))
                .loginType(LoginType.LOCAL)
                .build();

        userRepository.save(user);
    }

    @Override
    public String signIn(ReqSignupDto reqSignupDto) {
        User user = userRepository.findByUserId(reqSignupDto.userId())
                .orElseThrow(() -> new CustomException(StatusCode.NOT_EXIST_USER));

        if (user.getLoginType() != LoginType.LOCAL) {
            throw new CustomException(StatusCode.NOT_SUPPORT_LOCAL_LOGIN);
        }

        if (!passwordEncoder.matches(reqSignupDto.password(), user.getPassword())) {
            throw new CustomException(StatusCode.NOT_EXIST_USER);
        }
        return "";
    }
}
