package com.Guilherme.Warmify.repositories;

import com.Guilherme.Warmify.domain.facebookpage.FacebookPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FacebookPageRepository extends JpaRepository<FacebookPage, UUID> {
}

