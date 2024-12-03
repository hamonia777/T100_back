package org.example.t100.domain.openApi.controller;

import lombok.NoArgsConstructor;
import org.example.t100.domain.openApi.Dto.ChatGPTRequest;
import org.example.t100.domain.openApi.Dto.ChatGPTResponse;
import org.example.t100.domain.openApi.entity.OpenApi;
import org.example.t100.domain.openApi.repository.OpenApiRepository;
import org.example.t100.domain.openApi.service.OpenApiService;
import org.example.t100.global.Enum.SuccessCode;
import org.example.t100.global.dto.ApiResponse;
import org.example.t100.global.util.ResponseUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@RestController
@NoArgsConstructor
@RequestMapping("/api")
public class OpenApiController {
    @Autowired
    private OpenApiService openApiService;

    @GetMapping("/chat")
    public ApiResponse<?> call_GPT(){
        SuccessCode successCode = openApiService.create_Report();
        return ResponseUtils.ok(successCode);
    }
}
