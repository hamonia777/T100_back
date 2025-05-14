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
import org.example.t100.domain.community.repository.CommentRepository;
import org.example.t100.domain.community.repository.CommunitiyLikeRepository;
import org.example.t100.domain.community.repository.CommunityRepository;
import org.example.t100.global.Enum.ErrorCode;
import org.example.t100.global.Enum.SuccessCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.example.t100.global.Enum.SuccessCode.*;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final CommunitiyLikeRepository communitiyLikeRepository;
    private final CommentRepository commentRepository;

    //0428 게시글 저장 완성
    @Transactional
    public SuccessCode saveCommunity(CommunityRequestDto requestDto,User user) {
        Community community = new Community(requestDto,user);
        communityRepository.save(community);
        return COMMUNITY_SAVE_SUCCESS;
    }


    //만약 아이디를 변수로 받아 온다면 건드릴 필요 없을 듯?
    public CommunityResponseDto getCommunity(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException(ErrorCode.NOT_FOUND_DATA));
        community.setView(community.getView() + 1);
        communityRepository.save(community);
        return new CommunityResponseDto(community);
    }

    public List<CommunityResponseDto> getAllCommunity() {
        List<Community> community = communityRepository.findAll();
        List<CommunityResponseDto> communityResponseDtos= community.stream()
                        .map(commu-> new CommunityResponseDto(commu))
                                .toList();
        return communityResponseDtos;
    }

    //쓰지 않는 기능
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

    //이걸로 savecommunity를 할 때 사용하려고 했나 봄. 나는 그냥 만들어버림.
    public SuccessCode setCommunity(CommunityRequestDto requestDto, Long communityId,String email) {
        Community community = communityRepository.findById(communityId).orElseThrow(
                () -> new CommunityNotFoundException(ErrorCode.NOT_FOUND_DATA)
        );
        User user=userRepository.findById(community.getUser().getId()).orElse(null);

        if(email.equals(user.getEmail())){
            community.setCommunity(requestDto);
            communityRepository.save(community);
            return COMMUNITY_EDIT_SUCCESS;
        }
        else{
            return FAIL;
        }

    }

    //지우는 코드, 이것도 아이디를 받아 온다면 안 건드려도 될 듯
    public SuccessCode deleteCommunity(Long communityId) {
        Community community = communityRepository.findById(communityId).orElseThrow(
                () -> new CommunityNotFoundException(ErrorCode.NOT_FOUND_DATA)
        );
        //CommunityLike communityLike = communitiyLikeRepository.findByCommunityId(communityId);

        //communitiyLikeRepository.delete(communityLike);
        commentRepository.deleteAllByCommunityId(communityId);
        communityRepository.delete(community);

        return COMMUNITY_DELETE_SUCCESS;
    }
}
