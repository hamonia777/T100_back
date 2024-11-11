package org.example.t100.domain.login.service;

import lombok.RequiredArgsConstructor;
import org.example.t100.domain.Auth.entity.User;
import org.example.t100.domain.Auth.repository.UserRepository;
import org.example.t100.domain.login.dto.PrincipalDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public PrincipalDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userEntity = userRepository.findByEmail(username);
        if (userEntity.isPresent()) {
            User user = userEntity.get();
            return new PrincipalDetails(user.getEmail(),user.getPass(), user.getRole());
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
