package com.Guilherme.Warmify.domain.businessportfolio.dto;

import com.Guilherme.Warmify.utils.StatusBM;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BusinessPortfolioRegisterDTO(
        @NotBlank String bmName,
        @NotBlank String cnpj,
        String cnpjPdf,
        @NotNull StatusBM status,
        UUID domainId,
        @NotNull UUID facebookAccountId,
        UUID instagramAccountId) {
}

