package org.example.t100.domain.community.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.Auth.repository.UserRepository;
import org.example.t100.domain.community.dto.CommunityRequestDto;
import org.example.t100.domain.community.repository.CommunityRepository;
import org.example.t100.domain.community.service.CommunityService;
import org.example.t100.domain.login.jwt.JwtUtil;
import org.example.t100.global.Enum.SuccessCode;
import org.example.t100.global.dto.ApiResponse;
import org.example.t100.global.util.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommunityController {
    private final CommunityService communityService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

//    @GetMapping("/api/protected")
//    public ResponseEntity<?> protectedEndpoint(@CookieValue("accessToken") String accessToken) {
//        // 쿠키에서 accessToken 가져옴
//        if (jwtUtil.validateToken(accessToken)) {
//            // 토큰 유효하면 사용자 인증 성공
//            return ResponseEntity.ok("Access OK");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
//        }
//    }

    @PostMapping("/community")
    public ApiResponse<?> addCommunity(@CookieValue("accessToken") String accessToken,@RequestBody CommunityRequestDto requestDto){
        log.info("받은 accessToken: " + accessToken);
        if (jwtUtil.validateToken(accessToken)) {
            String username = jwtUtil.getUsername(accessToken);
            log.info("[*] username: " + username);
            Optional<User> cuser =userRepository.findByEmail(username);
            User user = cuser.orElse(null);
            return ResponseUtils.ok(communityService.saveCommunity(requestDto,user));
        }
        else{
            return ResponseUtils.ok(401);
        }

        //SuccessCode successCode = communityService.saveCommunity(requestDto);
        //return ResponseUtils.ok(successCode);
    }

    //원본 코드
//    @PostMapping("/community")
//    public ApiResponse<?> addCommunity(@RequestBody CommunityRequestDto requestDto){
//        SuccessCode successCode = communityService.saveCommunity(requestDto);
//        return ResponseUtils.ok(successCode);
//    }

    //이건 건드리지 않아도 괜찮
    @GetMapping("/community/{community_id}")
    public ApiResponse<?> getCommunity(@PathVariable("community_id") Long community_id){
        return ResponseUtils.ok(communityService.getCommunity(community_id));
    }

    //이것도 괜찮
    @PatchMapping("/community/{community_id}")
    public ApiResponse<?> updateCommunity(@PathVariable Long community_id,
                                          @RequestBody CommunityRequestDto requestDto,@CookieValue("accessToken") String accessToken)
    {
        if (jwtUtil.validateToken(accessToken)){
            String email = jwtUtil.getUsername(accessToken);


            return ResponseUtils.ok(communityService.setCommunity(requestDto, community_id,email));
        }


    }
//    @PatchMapping("/community/{community_id}")
//    public ApiResponse<?> updateCommunity(@PathVariable Long community_id,
//                                          @RequestBody CommunityRequestDto requestDto)
//    {
//        return ResponseUtils.ok(communityService.setCommunity(requestDto, community_id));
//    }
//    @PatchMapping("/community/{community_id}/postLike")
//    public ApiResponse<?> CommunityLike(@PathVariable Long community_id){
//        return ResponseUtils.ok(communityService.CommunityLike(community_id,1l));//userid는 추후에 쿠키에서 추출하는걸로 변경
//    }
    //이것도 괜찮
    @DeleteMapping("/community/{community_id}")
    public ApiResponse<?> deleteCommunity(@PathVariable Long community_id){
        return ResponseUtils.ok(communityService.deleteCommunity(community_id));
    }
}
