package com.Guilherme.Warmify.repositories;

import com.Guilherme.Warmify.domain.instagram.Instagram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstagramRepository extends JpaRepository<Instagram, UUID> {

    boolean existsByEmail(String email);
}
