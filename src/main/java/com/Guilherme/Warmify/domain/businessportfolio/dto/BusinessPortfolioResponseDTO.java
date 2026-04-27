package com.Guilherme.Warmify.domain.businessportfolio.dto;

import com.Guilherme.Warmify.domain.businessportfolio.BusinessPortfolio;
import com.Guilherme.Warmify.utils.StatusBM;

import java.util.List;
import java.util.UUID;

public record BusinessPortfolioResponseDTO(
        UUID id,
        String bmName,
        String cnpj,
        String cnpjPdf,
        List<UUID> facebookPages,
        StatusBM status,
        UUID domainId,
        UUID facebookAccountId,
        UUID instagramAccountId) {

    public BusinessPortfolioResponseDTO(BusinessPortfolio businessPortfolio) {
        this(
                businessPortfolio.getId(),
                businessPortfolio.getBmName(),
                businessPortfolio.getCnpj(),
                businessPortfolio.getCnpjPdf(),
                businessPortfolio.getFacebookPages(),
                businessPortfolio.getStatus(),
                businessPortfolio.getDomain() != null ? businessPortfolio.getDomain().getId() : null,
                businessPortfolio.getFacebookAccount() != null ? businessPortfolio.getFacebookAccount().getId() : null,
                businessPortfolio.getInstagramAccount() != null ? businessPortfolio.getInstagramAccount().getId() : null
        );
    }
}

