package org.example.t100.domain.Auth.repository;

import org.example.t100.domain.Auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNick(String nick);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email); // 수정된 메서드 이름
}
