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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.example.t100.global.Enum.SuccessCode.*;
import static org.example.t100.global.Enum.SuccessCode.COMMUNITY_DELETE_SUCCESS;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public SuccessCode saveComment(CommentDto commentDto,User user,Community community) {
        Comment comment = new Comment(commentDto, user , community);
        commentRepository.save(comment);
        return COMMUNITY_SAVE_SUCCESS;
    }


    public List<CommentDto> getComment(Long communityId) {
        List<Comment> comment = commentRepository.findByCommunityId(communityId);
        List<CommentDto> commentDtos= comment.stream()
                .map(comm -> new CommentDto(comm))
                .collect(Collectors.toList());
        return commentDtos;
    }


    //이걸로 savecommunity를 할 때 사용하려고 했나 봄. 나는 그냥 만들어버림.
    @Transactional
    public SuccessCode setComment(CommentDto commentDto, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CommunityNotFoundException(ErrorCode.NOT_FOUND_DATA)
        );
        if(commentId.equals(comment.getId())){
            comment.setComment(commentDto);
            commentRepository.save(comment);
            return COMMUNITY_EDIT_SUCCESS;
        }
        else{
            return FAIL;
        }
    }

    //지우는 코드, 이것도 아이디를 받아 온다면 안 건드려도 될 듯
    @Transactional
    public SuccessCode deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CommunityNotFoundException(ErrorCode.NOT_FOUND_DATA)
        );
        //CommunityLike communityLike = communitiyLikeRepository.findByCommunityId(communityId);

        //communitiyLikeRepository.delete(communityLike);
        commentRepository.delete(comment);

        return COMMUNITY_DELETE_SUCCESS;
    }
}
