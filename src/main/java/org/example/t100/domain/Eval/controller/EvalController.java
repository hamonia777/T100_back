package org.example.t100.domain.Eval.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.t100.domain.Eval.dto.EvalDto;
import org.example.t100.domain.Eval.repository.EvalRepository;
import org.example.t100.domain.Eval.service.EvalService;
import org.example.t100.domain.community.dto.CommunityRequestDto;
import org.example.t100.global.dto.ApiResponse;
import org.example.t100.global.util.ResponseUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EvalController {
    private final EvalService evalService;
    private final EvalRepository evalRepository;

    @PostMapping("/eval")
    public ApiResponse<?> addEval(@RequestBody EvalDto evalDto){
        return ResponseUtils.ok(evalService.saveEval(evalDto));
    }

    @GetMapping("/eval/all")
    public ApiResponse<?> getEval(){
        return ResponseUtils.ok(evalService.getEval());
    }

    @GetMapping("/eval/all/ave")
    public ApiResponse<?> getAve(){
        return ResponseUtils.ok(evalService.getAve());
    }

}
