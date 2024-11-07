package org.example.t100.domain.Auth.service;

import lombok.RequiredArgsConstructor;
import org.example.t100.domain.Auth.dto.SignupRequestDto;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.Auth.repository.UserRepository;
import org.example.t100.global.Enum.SuccessCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static org.example.t100.global.Enum.SuccessCode.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SuccessCode signup(SignupRequestDto requestDto) {
        User user = new User(requestDto);
        user.setPass(bCryptPasswordEncoder.encode(requestDto.getPass()));
        userRepository.save(user);
        user.setRole("NORMAL");
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
}
