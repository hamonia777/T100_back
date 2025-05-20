package org.example.t100.domain.community.repository;

import org.example.t100.domain.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByCommunityId(Long communityId);
    List<Comment> findAllByUserId(Long userId);
    void deleteAllByCommunityId(Long communityId);
}
