package org.example.t100.domain.test.dto;

import lombok.Getter;
import lombok.Setter;
import net.bytebuddy.asm.Advice;

import java.time.LocalDateTime;

@Getter
@Setter
public class TestResponseDto {
    Long id;
    String data;
    LocalDateTime createdAt;
    public TestResponseDto(Long id, String data, LocalDateTime createdAt) {
        this.id = id;
        this.data = data;
        this.createdAt = createdAt;
    }
}
