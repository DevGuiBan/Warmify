package com.Guilherme.Warmify.domain.facebook.dto;

import com.Guilherme.Warmify.domain.facebook.Facebook;
import com.Guilherme.Warmify.utils.StatusAccount;

import java.util.UUID;

public record FacebookResponseDTO (UUID id, String profileName, String email, String url, StatusAccount statusAccount){

    public FacebookResponseDTO(Facebook facebook) {
        this(
                facebook.getId(),
                facebook.getProfileName(),
                facebook.getEmail(),
                facebook.getUrl(),
                facebook.getStatusAccount()
        );
    }
}
