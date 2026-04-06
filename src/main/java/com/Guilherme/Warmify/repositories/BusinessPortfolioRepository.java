package com.Guilherme.Warmify.repositories;

import com.Guilherme.Warmify.domain.businessportfolio.BusinessPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BusinessPortfolioRepository extends JpaRepository<BusinessPortfolio, UUID> {
}

