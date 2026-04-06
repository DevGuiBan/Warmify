package com.Guilherme.Warmify.domain.domain.dto;


import com.Guilherme.Warmify.domain.domain.Domain;

import java.util.UUID;

public record DomainResponseDTO(UUID id, String url) {

    public DomainResponseDTO(Domain domain) {
        this(domain.getId(), domain.getDomUrl());
    }
}
