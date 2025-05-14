package org.example.t100.domain.community.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.t100.domain.community.entity.Comment;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private Long community_id;
    private String content;
    private String nick;
    private LocalDateTime created_at;

    public CommentDto(Comment comm){
        this.content=comm.getContent();
        this.nick=comm.getUser().getNick();
        this.community_id=comm.getCommunity().getId();
        this.created_at=comm.getCommentAt();
    }
}


