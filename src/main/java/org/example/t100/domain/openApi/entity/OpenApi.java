package org.example.t100.domain.openApi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class OpenApi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;
    String title;
    String content;
}
