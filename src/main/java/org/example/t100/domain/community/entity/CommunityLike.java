package org.example.t100.domain.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.t100.domain.Auth.entity.User;

@Entity
@NoArgsConstructor
@Getter
public class CommunityLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String nick;

    @ManyToOne
    Community community;

    @ManyToOne
    User user;

    public CommunityLike(Community community, User user) {
        this.community = community;
        this.user = user;
        this.nick = user.getNick();
    }
}
