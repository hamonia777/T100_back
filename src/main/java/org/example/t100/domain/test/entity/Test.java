package org.example.t100.domain.test.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.t100.global.util.timestamp.Timestamped;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Test extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String data;

    public Test(String data) {
        this.data = data;
    }
}
