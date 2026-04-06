package com.Guilherme.Warmify.repositories;

import com.Guilherme.Warmify.domain.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    UserDetails findByEmail(String email);
    Optional<User> findUserByEmail(String email);
    boolean existsByEmail(String email);
}
