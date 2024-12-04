package org.example.t100.domain.Auth.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.t100.domain.Auth.dto.SignupRequestDto;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.Auth.service.MailService;
import org.example.t100.domain.Auth.service.UserService;
import org.example.t100.global.dto.ApiResponse;
import org.example.t100.global.util.ResponseUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final MailService mailService;

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
}
