package com.Guilherme.Warmify.domain.facebook.dto;

import jakarta.validation.constraints.NotBlank;

public record FacebookRegisterDTO (
        @NotBlank String profileName,
        @NotBlank String email,
        @NotBlank String password,
        @NotBlank String url) {
}
