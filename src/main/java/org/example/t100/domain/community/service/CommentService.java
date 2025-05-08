package org.example.t100.domain.community.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.Auth.exception.UserNotFoundException;
import org.example.t100.domain.Auth.repository.UserRepository;
import org.example.t100.domain.community.dto.CommentDto;
import org.example.t100.domain.community.dto.CommunityRequestDto;
import org.example.t100.domain.community.dto.CommunityResponseDto;
import org.example.t100.domain.community.entity.Comment;
import org.example.t100.domain.community.entity.Community;
import org.example.t100.domain.community.entity.CommunityLike;
import org.example.t100.domain.community.exception.CommunityNotFoundException;
import org.example.t100.domain.community.repository.CommentRepository;
import org.example.t100.domain.community.repository.CommunitiyLikeRepository;
import org.example.t100.domain.community.repository.CommunityRepository;
import org.example.t100.global.Enum.ErrorCode;
import org.example.t100.global.Enum.SuccessCode;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.example.t100.global.Enum.SuccessCode.*;
import static org.example.t100.global.Enum.SuccessCode.COMMUNITY_DELETE_SUCCESS;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    //0428 게시글 저장 완성
    @Transactional
    public SuccessCode saveComment(CommentDto commentDto, User user,Community community) {

        Comment comment = new Comment(commentDto,user,community);
        commentRepository.save(comment);
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
        Community community = commentRepository.findById(communityId).orElseThrow(
                () -> new CommunityNotFoundException(ErrorCode.NOT_FOUND_DATA)
        );
        communityRepository.delete(community);

        return COMMUNITY_DELETE_SUCCESS;
    }
}
