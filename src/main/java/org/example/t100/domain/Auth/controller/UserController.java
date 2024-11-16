package org.example.t100.domain.Auth.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.t100.domain.Auth.dto.SignupRequestDto;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.Auth.service.UserService;
import org.example.t100.global.dto.ApiResponse;
import org.example.t100.global.util.ResponseUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponse<?> signup(@RequestBody SignupRequestDto requestDto) {
        return ResponseUtils.ok(userService.signup(requestDto));
    }

    @GetMapping("/signUp/checkNick")
    public ApiResponse<?> checkNick(@RequestParam String nick) {
        return ResponseUtils.ok(userService.checkNick(nick));
    }

    @GetMapping("/signUp/findMyPass")
    public ApiResponse<?> findPass(@RequestParam String pass) {
        return ResponseUtils.ok(userService.findPass(pass));
    }
}
