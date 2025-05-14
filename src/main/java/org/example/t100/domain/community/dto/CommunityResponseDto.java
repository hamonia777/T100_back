package org.example.t100.domain.community.dto;

import lombok.Getter;
import org.example.t100.domain.community.entity.Community;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
public class CommunityResponseDto {
    private long community_id;
    private String nick;
    private String title;
    private String content;
    private long view;
    private LocalDateTime created_at;

    public CommunityResponseDto(Community community) {
        this.community_id = community.getId();
        this.title = community.getTitle();
        this.content = community.getContent();
        this.view = community.getView();
        this.nick = community.getUser().getNick();
        this.created_at = community.getCommunityAt();
    }
}
