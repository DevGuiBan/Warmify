package com.Guilherme.Warmify.repositories;

import com.Guilherme.Warmify.domain.numberportfolio.NumberPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NumberPortfolioRepository extends JpaRepository<NumberPortfolio, UUID> {

    List<NumberPortfolio> findByBusinessPortfolioId(UUID businessPortfolioId);
}

