package org.example.t100.domain.community.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.community.dto.CommentDto;
import org.example.t100.domain.community.dto.CommunityRequestDto;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;
    String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "community_id")
    private Community community;



    @ManyToOne(fetch = FetchType.EAGER) // 기본적으로 LAZY 추천 LAZY는 사용할 때 객체 생성/EAGER는 즉시 객체 생성
    @JoinColumn(name = "user_id") // DB에 저장될 외래키 컬럼명
    private User user;

    public void setCommunity(CommentDto commentDto)
    {
        this.title = commentDto.getTitle();
        this.content = commentDto.getContent();
    }

    public Comment(CommentDto commentDto, User user,Community community) {
        this.user = user;
        this.title = commentDto.getTitle();
        this.content = commentDto.getContent();
        this.community = community;
    }
}
