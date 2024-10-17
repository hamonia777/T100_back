package org.example.t100.domain.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.community.dto.CommunityRequestDto;
import org.example.t100.global.timestamp.Timestamped;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Community extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;
    String content;
    Long view;

    @ManyToOne
    User user;
//    @OneToMany(fetch = FetchType.EAGER)
//    @JoinColumn(name = "community_id")
//    CommunityLike communityLike;
//
    public void setCommunity(CommunityRequestDto requestDto)
    {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
    }
    public Community(CommunityRequestDto requestDto, User user) {
        this.user = user;
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.view = 0L;
    }
}
