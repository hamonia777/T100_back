package org.example.t100.domain.community.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.Auth.exception.UserNotFoundException;
import org.example.t100.domain.Auth.repository.UserRepository;
import org.example.t100.domain.community.dto.CommunityRequestDto;
import org.example.t100.domain.community.dto.CommunityResponseDto;
import org.example.t100.domain.community.entity.Community;
import org.example.t100.domain.community.entity.CommunityLike;
import org.example.t100.domain.community.exception.CommunityNotFoundException;
import org.example.t100.domain.community.repository.CommunitiyLikeRepository;
import org.example.t100.domain.community.repository.CommunityRepository;
import org.example.t100.global.Enum.ErrorCode;
import org.example.t100.global.Enum.SuccessCode;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.example.t100.global.Enum.SuccessCode.*;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final CommunitiyLikeRepository communitiyLikeRepository;

    @Transactional
    public SuccessCode saveCommunity(CommunityRequestDto requestDto) {
        User user = new User("홍길동", "test@gmail.com", "password", "01012345678", "20010421");
        userRepository.save(user);
        Community community = new Community(requestDto, user);
        communityRepository.save(community);
        return COMMUNITY_SAVE_SUCCESS;
    }

    public CommunityResponseDto getCommunity(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException(ErrorCode.NOT_FOUND_DATA));
        community.setView(community.getView() + 1);
        communityRepository.save(community);
        return new CommunityResponseDto(community);
    }

    public SuccessCode CommunityLike(Long communityId,Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException(ErrorCode.NOT_FOUND_DATA));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.NOT_FOUND_DATA));
        if(communitiyLikeRepository.existsByUserIdAndCommunityId(userId, communityId)) {
            Optional<CommunityLike> communityLike = communitiyLikeRepository.findByUserIdAndCommunityId(userId, communityId);
            communitiyLikeRepository.delete(communityLike.get());
            return LIKE_CANCEL;
        }
        else
        {
            CommunityLike communityLike = new CommunityLike(community,user);
            communitiyLikeRepository.save(communityLike);
            return  LIKE_SUCCESS;
        }
    }

    public SuccessCode setCommunity(CommunityRequestDto requestDto, Long communityId) {
        Community community = communityRepository.findById(communityId).orElseThrow(
                () -> new CommunityNotFoundException(ErrorCode.NOT_FOUND_DATA)
        );

        community.setCommunity(requestDto);
        communityRepository.save(community);
        return COMMUNITY_EDIT_SUCCESS;
    }

    public SuccessCode deleteCommunity(Long communityId) {
        Community community = communityRepository.findById(communityId).orElseThrow(
                () -> new CommunityNotFoundException(ErrorCode.NOT_FOUND_DATA)
        );
        CommunityLike communityLike = communitiyLikeRepository.findByCommunityId(communityId);

        communitiyLikeRepository.delete(communityLike);
        communityRepository.delete(community);

        return COMMUNITY_DELETE_SUCCESS;
    }
}
