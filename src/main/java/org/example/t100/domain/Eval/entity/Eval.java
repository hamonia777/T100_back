package org.example.t100.domain.Eval.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.t100.domain.Eval.dto.EvalDto;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Eval{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long score;
    String content;

    public void setEval(EvalDto evalDto)
    {
        this.score = evalDto.getScore();
        this.content = evalDto.getContent();
    }

    public Eval(EvalDto evalDto) {
        this.score = evalDto.getScore();
        this.content = evalDto.getContent();
    }

    public int getScoreAve(){
        return score.intValue();
    }

    @PrePersist
    public void prePersist() {
        if (this.evalAt == null) {  // 만약 reportAt이 null이면
            this.evalAt = LocalDateTime.now();  // 자동으로 현재 시간 삽입
        }
    }
    LocalDateTime evalAt = LocalDateTime.now();

}


