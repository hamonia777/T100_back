package org.example.t100.domain.Auth.service;

import lombok.RequiredArgsConstructor;
import org.example.t100.domain.Auth.dto.SignupRequestDto;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.Auth.repository.UserRepository;
import org.example.t100.global.Enum.SuccessCode;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static org.example.t100.global.Enum.SuccessCode.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SuccessCode signup(SignupRequestDto requestDto) {
        User user = new User(requestDto); //dto->entity 변환
        user.setPass(bCryptPasswordEncoder.encode(requestDto.getPass()));  //비밀번호 암호화
        userRepository.save(user); //db에 저장
        user.setRole("NORMAL"); // 권한 설정
        return SIGNUP_SUCCESS;
    }

    public SuccessCode checkNick(String nick) {
        if(userRepository.existsByNick(nick)){
            return DUPLICATE_NICK;
        }
        else{
            return NOT_DUPLICATE_NICK;
        }
    }


    public SuccessCode checkEmail(String email) {
        if(userRepository.existsByEmail(email)){
            return DUPLICATE_EMAIL;
        }
        else
            return NOT_DUPLICATE_EMAIL;
    }
}
