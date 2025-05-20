package org.example.t100.domain.Auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.t100.domain.Auth.entity.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyPageDto {
    private Long id;
    private String nick;
    private String email;
    private String pass;
    private String phone;
    private String birth;
    private String role = "NORMAL";

    public MyPageDto(User user){
        this.id = user.getId();
        this.nick = user.getNick();
        this.email = user.getEmail();
        this.pass = user.getPass();
        this.phone = user.getPhone();
        this.birth = user.getBirth();
        this.role = user.getRole();
    }

}
