package org.example.t100.domain.community.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {
    private Long id;
    private String title;
    private String content;
}
