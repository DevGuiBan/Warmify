package com.Guilherme.Warmify.repositories;


import com.Guilherme.Warmify.domain.domain.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DomainRepository extends JpaRepository<Domain, UUID> {

    Domain findByDomUrl(String domUrl);
}