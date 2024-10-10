package org.example.t100.domain.community.controller;

import lombok.RequiredArgsConstructor;
import org.example.t100.domain.community.dto.CommunityRequestDto;
import org.example.t100.domain.community.repository.CommunityRepository;
import org.example.t100.domain.community.service.CommunityService;
import org.example.t100.global.Enum.SuccessCode;
import org.example.t100.global.dto.ApiResponse;
import org.example.t100.global.util.ResponseUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommunityController {
    private final CommunityService communityService;

    @PostMapping("/community")
    public ApiResponse<?> addCommunity(@RequestBody CommunityRequestDto requestDto){
        SuccessCode successCode = communityService.saveCommunity(requestDto);
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/community/{community_id}")
    public ApiResponse<?> getCommunity(@PathVariable Long community_id){
        return ResponseUtils.ok(communityService.getCommunity(community_id));
    }
}
