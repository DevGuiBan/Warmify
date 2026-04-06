package com.Guilherme.Warmify.repositories;

import com.Guilherme.Warmify.domain.facebook.Facebook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FacebookRepository extends JpaRepository<Facebook, UUID> {

    Facebook findByEmail(String email);
    boolean existsByEmail(String email);
}
