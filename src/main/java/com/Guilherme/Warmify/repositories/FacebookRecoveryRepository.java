package com.Guilherme.Warmify.repositories;

import com.Guilherme.Warmify.domain.recoveryKeys.FacebookRecovery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FacebookRecoveryRepository extends JpaRepository<FacebookRecovery, UUID> {

    List<FacebookRecovery> findByFacebookAccountId(UUID facebookAccountId);
}

