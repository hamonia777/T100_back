package org.example.t100.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.t100.domain.community.entity.Comment;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long comment_id;
    private Long community_id;
    private String content;
    private String nick;
    private String mail;
    private LocalDateTime created_at;

    public CommentDto(Comment comm){
        this.comment_id = comm.getId();
        this.content=comm.getContent();
        this.nick=comm.getUser().getNick();
        //this.mail = comm.getUser().getEmail();
        this.community_id=comm.getCommunity().getId();
        this.created_at=comm.getCommentAt();
    }
}


