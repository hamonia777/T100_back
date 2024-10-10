package org.example.t100.domain.community.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.t100.global.util.timestamp.Timestamped;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Community extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Community_id;

    String nick;
    String title;
    String content;
    Long view;

}
