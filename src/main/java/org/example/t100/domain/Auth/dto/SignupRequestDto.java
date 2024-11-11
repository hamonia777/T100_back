package org.example.t100.domain.Auth.dto;

import lombok.Getter;

@Getter
public class SignupRequestDto {
    private String nick;
    private String email;
    private String pass;
    private String phone;
    private String birth;
    private String role = "NORMAL";
}
