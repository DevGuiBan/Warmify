package com.Guilherme.Warmify.repositories;

import com.Guilherme.Warmify.domain.recoveryKeys.InstagramRecovery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InstagramRecoveryRepository extends JpaRepository<InstagramRecovery, UUID> {

    List<InstagramRecovery> findByInstagramAccountId(UUID instagramAccountId);
}

