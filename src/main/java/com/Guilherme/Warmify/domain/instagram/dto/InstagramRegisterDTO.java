package com.Guilherme.Warmify.domain.instagram.dto;

import jakarta.validation.constraints.NotBlank;

public record InstagramRegisterDTO(
        @NotBlank String username,
        @NotBlank String email,
        @NotBlank String password,
        @NotBlank String googleAuthenticatorEmail) {
}
