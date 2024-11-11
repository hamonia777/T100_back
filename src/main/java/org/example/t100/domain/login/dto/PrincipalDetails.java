package org.example.t100.domain.login.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.t100.domain.Auth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PrincipalDetails implements UserDetails {

    private final String username;
    private final String password;
    private final String roles;

    public PrincipalDetails(String username, String password, String roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles != null ?
                List.of(roles.split(",")).stream()
                        .filter(role -> role != null && !role.trim().isEmpty()) // null 또는 빈 문자열이 아닌 경우만 추가
                        .map(String::trim)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
                : new ArrayList<>();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 사이트에서 1년 동안 회원이 로그인을 안하면 -> 휴면 계정으로 전환하는 로직이 있다고 치자
        // user entity의 field에 "Timestamp loginDate"를 하나 만들어주고
        // (현재 시간 - loginDate) > 1년 -> return false; 로 설정
        return true;
    }
}