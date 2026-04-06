package com.Guilherme.Warmify.domain.users.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(
        @NotBlank String email,
        @NotBlank String password) {
}
