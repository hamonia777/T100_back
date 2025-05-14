package org.example.t100.domain.community.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.community.dto.CommentDto;
import org.example.t100.domain.community.dto.CommunityRequestDto;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "community_id")
    private Community community;

    @ManyToOne(fetch = FetchType.EAGER) // 기본적으로 LAZY 추천 LAZY는 사용할 때 객체 생성/EAGER는 즉시 객체 생성
    @JoinColumn(name = "user_id") // DB에 저장될 외래키 컬럼명
    private User user;

    @PrePersist
    public void prePersist() {
        if (this.commentAt == null) {  // 만약 reportAt이 null이면
            this.commentAt = LocalDateTime.now();  // 자동으로 현재 시간 삽입
        }
    }
    LocalDateTime commentAt = LocalDateTime.now();



    public void setComment(CommentDto commentDto)
    {
        this.content = commentDto.getContent();
    }

    public Comment(CommentDto commentDto, User user,Community community) {
        this.user = user;
        this.content = commentDto.getContent();
        this.community = community;
    }
}
