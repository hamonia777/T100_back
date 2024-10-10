package org.example.t100.domain.community.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.Auth.repository.UserRepository;
import org.example.t100.domain.community.dto.CommunityRequestDto;
import org.example.t100.domain.community.dto.CommunityResponseDto;
import org.example.t100.domain.community.entity.Community;
import org.example.t100.domain.community.exception.CommunityNotFoundException;
import org.example.t100.domain.community.repository.CommunityRepository;
import org.example.t100.global.Enum.ErrorCode;
import org.example.t100.global.Enum.SuccessCode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;


    @Transactional
    public SuccessCode saveCommunity(CommunityRequestDto requestDto) {
        User user = new User("홍길동", "test@gmail.com", "password", "01012345678", "20010421");
        userRepository.save(user);
        Community community = new Community(requestDto, user);
        communityRepository.save(community);
        return SuccessCode.COMMUNITY_SAVE_SUCCESS;
    }

    public CommunityResponseDto getCommunity(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException(ErrorCode.NOT_FOUND_DATA));
        return new CommunityResponseDto(community);
    }
}
