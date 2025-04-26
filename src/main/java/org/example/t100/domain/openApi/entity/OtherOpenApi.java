package org.example.t100.domain.openApi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class OtherOpenApi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;
    String title;
    @Column(columnDefinition = "LONGTEXT")
    String content;

    @PrePersist
    public void prePersist() {
        if (this.reportAt == null) {  // 만약 reportAt이 null이면
            this.reportAt = LocalDateTime.now();  // 자동으로 현재 시간 삽입
        }
    }
    LocalDateTime reportAt = LocalDateTime.now();

}
