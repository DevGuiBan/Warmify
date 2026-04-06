package com.Guilherme.Warmify.domain.instagram.dto;

import com.Guilherme.Warmify.domain.instagram.Instagram;
import com.Guilherme.Warmify.utils.StatusAccount;

import java.util.UUID;

public record InstagramResponseDTO(UUID id, String username, String email, String googleAuthenticatorEmail, StatusAccount statusAccount) {
    public InstagramResponseDTO(Instagram instagram) {
        this(
                instagram.getId(),
                instagram.getUsername(),
                instagram.getEmail(),
                instagram.getGoogleAuthenticatorEmail(),
                instagram.getStatusAccount()
        );
    }
}
