package com.Guilherme.Warmify.domain.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record DomainRegisterDTO(@NotBlank String domUrl) {
}
