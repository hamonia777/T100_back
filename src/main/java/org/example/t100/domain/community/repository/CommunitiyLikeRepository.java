package org.example.t100.domain.community.repository;

import org.example.t100.domain.community.entity.CommunityLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunitiyLikeRepository extends JpaRepository<CommunityLike, Long> {
    Optional findByUserIdAndCommunityId(Long userId,Long communityId);
    Boolean existsByUserIdAndCommunityId(Long userId,Long communityId);
    CommunityLike findByCommunityId(Long communityId);
}
