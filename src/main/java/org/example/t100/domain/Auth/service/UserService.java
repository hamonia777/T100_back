package org.example.t100.domain.Auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.t100.domain.Auth.dto.MyPageDto;
import org.example.t100.domain.Auth.dto.SignupRequestDto;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.Auth.repository.UserRepository;
import org.example.t100.domain.community.dto.CommentDto;
import org.example.t100.domain.community.dto.CommunityResponseDto;
import org.example.t100.domain.community.entity.Comment;
import org.example.t100.domain.community.entity.Community;
import org.example.t100.domain.community.repository.CommentRepository;
import org.example.t100.domain.community.repository.CommunityRepository;
import org.example.t100.domain.login.jwt.JwtUtil;
import org.example.t100.global.Enum.SuccessCode;
import org.example.t100.global.util.ResponseUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.example.t100.global.Enum.SuccessCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final JwtUtil jwtUtil;
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;


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

    public Object getInfo(String accessToken) {
        log.info("받은 accessToken: " + accessToken);
        if (jwtUtil.validateToken(accessToken)) {
            String username = jwtUtil.getUsername(accessToken);
            log.info("[*] username: " + username);
            Optional<User> cuser =userRepository.findByEmail(username);
            User user = cuser.orElse(null);
            return new MyPageDto(user);
        }
        else{
            return ResponseUtils.ok(401);
        }
    }

    public Object changePass(String accessToken, Map<String, Object> data) {
        log.info("받은 accessToken: " + accessToken);
        if (jwtUtil.validateToken(accessToken)) {
            String username = jwtUtil.getUsername(accessToken);
            log.info("[*] username: " + username);
            Optional<User> cuser =userRepository.findByEmail(username);
            User user = cuser.orElse(null);
            String pass=(String)data.get("pass");
            //pass.equals(user.getPass())
            if(bCryptPasswordEncoder.matches(pass, user.getPass())){
                String newPass=(String)data.get("newPass");
                user.setPass(bCryptPasswordEncoder.encode(newPass));
                return userRepository.save(user);
            }
            else{
                return ResponseUtils.ok(401);
            }

        }
        else{
            return ResponseUtils.ok(401);
        }

    }


    public Object changeNick(String accessToken, Map<String, Object> data) {
        log.info("받은 accessToken: " + accessToken);
        if (jwtUtil.validateToken(accessToken)) {
            String username = jwtUtil.getUsername(accessToken);
            log.info("[*] username: " + username);
            Optional<User> cuser =userRepository.findByEmail(username);
            User user = cuser.orElse(null);
            user.setNick((String)data.get("newNick"));
            return userRepository.save(user);
        }
        else{
            return ResponseUtils.ok(401);
        }

    }

    public Object myCommunity(String accessToken) {
        log.info("받은 accessToken: " + accessToken);
        if (jwtUtil.validateToken(accessToken)) {
            String username = jwtUtil.getUsername(accessToken);
            log.info("[*] username: " + username);
            Optional<User> cuser =userRepository.findByEmail(username);
            User user = cuser.orElse(null);
            List<Community> dtos =communityRepository.findAllByUserId(user.getId());
            List<CommunityResponseDto> Rdtos=dtos.stream().map(comm-> new CommunityResponseDto(comm))
                    .collect(Collectors.toList());
            return Rdtos;
        }
        else{
            return ResponseUtils.ok(401);
        }
    }

    public Object myComment(String accessToken) {
        log.info("받은 accessToken: " + accessToken);
        if (jwtUtil.validateToken(accessToken)) {
            String username = jwtUtil.getUsername(accessToken);
            log.info("[*] username: " + username);
            Optional<User> cuser =userRepository.findByEmail(username);
            User user = cuser.orElse(null);
            List<Comment> dtos =commentRepository.findAllByUserId(user.getId());
            List<CommentDto> Rdtos=dtos.stream().map(comm-> new CommentDto(comm))
                    .collect(Collectors.toList());
            return Rdtos;
        }
        else{
            return ResponseUtils.ok(401);
        }
    }
}
