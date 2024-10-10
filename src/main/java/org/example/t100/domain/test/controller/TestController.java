package org.example.t100.domain.test.controller;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.example.t100.domain.test.dto.TestRequestDto;
import org.example.t100.domain.test.dto.TestResponseDto;
import org.example.t100.domain.test.service.TestService;
import org.example.t100.global.dto.ApiResponse;

import org.example.t100.global.util.ResponseUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TestController {
    private final TestService testService;
    @GetMapping("/test")
    public ApiResponse<?> test() {
        return ResponseUtils.ok(testService.getTest());
    }
    @PostMapping("/testSave")
    public void testSave(@RequestBody TestRequestDto testRequestDto) {
        testService.setTest(testRequestDto);
    }
}
