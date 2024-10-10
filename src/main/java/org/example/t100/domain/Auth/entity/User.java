package org.example.t100.domain.Auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.t100.domain.community.entity.Community;
import org.example.t100.global.timestamp.Timestamped;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String nick;
    String email;
    String pass;
    String phone;
    String birth;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    List<Community> communities;

    public User(String nick, String email, String pass, String phone, String birth) {
        this.nick = nick;
        this.email = email;
        this.pass = pass;
        this.phone = phone;
        this.birth = birth;
    }
}
