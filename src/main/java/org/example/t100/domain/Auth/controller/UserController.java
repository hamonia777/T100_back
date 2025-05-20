package org.example.t100.domain.Auth.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.t100.domain.Auth.dto.SignupRequestDto;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.Auth.repository.UserRepository;
import org.example.t100.domain.Auth.service.MailService;
import org.example.t100.domain.Auth.service.UserService;
import org.example.t100.domain.login.jwt.JwtUtil;
import org.example.t100.global.dto.ApiResponse;
import org.example.t100.global.util.ResponseUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final MailService mailService;
    //private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ApiResponse<?> signup(@RequestBody SignupRequestDto requestDto) {
        return ResponseUtils.ok(userService.signup(requestDto));
    }

    @GetMapping("/signup/checkNick")
    public ApiResponse<?> checkNick(@RequestParam("nick") String nick) {
        return ResponseUtils.ok(userService.checkNick(nick));
    }

    @GetMapping("/signup/checkEmail")
    public ApiResponse<?> checkEmail(@RequestParam("email") String email) {
        return ResponseUtils.ok(userService.checkEmail(email));
    }

    @GetMapping("/signup/findMyPass")
    public ApiResponse<?> findPass(@RequestParam("Email") String Email) {
        return ResponseUtils.ok(mailService.findPass(Email));
    }

    @GetMapping("/myinfo")
    public ApiResponse<?> myinfo(@CookieValue("accessToken") String accessToken){
        return ResponseUtils.ok(userService.getInfo(accessToken));
    }

    @PatchMapping("/passChange")
    public ApiResponse<?> passChange(@CookieValue("accessToken") String accessToken,@RequestBody Map<String,Object> data){
        return ResponseUtils.ok(userService.changePass(accessToken,data));
    }

    @PatchMapping("/nickChange")
    public ApiResponse<?> nickChange(@CookieValue("accessToken") String accessToken,@RequestBody Map<String,Object> data){
        return ResponseUtils.ok(userService.changeNick(accessToken,data));

    }

    @GetMapping("/myCommunity")
    public ApiResponse<?> myCommunity(@CookieValue("accessToken") String accessToken){
        return ResponseUtils.ok(userService.myCommunity(accessToken));

    }
    @GetMapping("/myComment")
    public ApiResponse<?> myComment(@CookieValue("accessToken") String accessToken){
        return ResponseUtils.ok(userService.myComment(accessToken));
    }

}
