package org.example.t100.domain.login.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.Auth.repository.UserRepository;
import org.example.t100.domain.community.exception.CommunityNotFoundException;
import org.example.t100.domain.login.dto.CustomUserDetails;
import org.example.t100.global.Enum.ErrorCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {


    private final UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User userData = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("NOT_FOUND_DATA"));;
        if (username != null) {
            return new CustomUserDetails(userData);
        }

        return null;
    }


}