package com.Guilherme.Warmify.domain.domain.dto;


import com.Guilherme.Warmify.domain.domain.Domain;

public record DomainResponseDTO(String url) {

    public DomainResponseDTO(Domain domain) {
        this(domain.getDomUrl());
    }
}
