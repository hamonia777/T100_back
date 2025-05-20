package org.example.t100.domain.Eval.service;

import lombok.RequiredArgsConstructor;
import org.example.t100.domain.Eval.dto.EvalDto;
import org.example.t100.domain.Eval.entity.Eval;
import org.example.t100.domain.Eval.repository.EvalRepository;
import org.example.t100.domain.community.exception.CommunityNotFoundException;
import org.example.t100.global.Enum.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import static org.example.t100.global.Enum.SuccessCode.COMMUNITY_SAVE_SUCCESS;

@Service
@RequiredArgsConstructor
public class EvalService {
    private final EvalRepository evalRepository;


    public Object saveEval(EvalDto evalDto) {
        Eval eval= new Eval(evalDto);
        evalRepository.save(eval);
        return COMMUNITY_SAVE_SUCCESS;
    }

    public Object getEval() {
        List<Eval> evals = evalRepository.findAll();
        List<EvalDto> evalDtos = evals.stream()
                .map(eval -> new EvalDto(eval.getScore(), eval.getContent()))
                .collect(Collectors.toList());
        return evalDtos;

    }

    public Object getAve() {
        List<Eval> evals = evalRepository.findAll();
        double ave = evals.stream()
                .mapToInt(Eval::getScoreAve)  // score 값을 int로 매핑
                .average()                 // 평균 계산
                .orElse(0);                // 값이 없을 때 0으로 대체
        return ave;
    }
}
