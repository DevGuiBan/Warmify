package com.Guilherme.Warmify.controllers;

import com.Guilherme.Warmify.domain.businessportfolio.BusinessPortfolio;
import com.Guilherme.Warmify.domain.businessportfolio.dto.BusinessPortfolioRegisterDTO;
import com.Guilherme.Warmify.domain.businessportfolio.dto.BusinessPortfolioResponseDTO;
import com.Guilherme.Warmify.domain.domain.Domain;
import com.Guilherme.Warmify.domain.facebook.Facebook;
import com.Guilherme.Warmify.domain.instagram.Instagram;
import com.Guilherme.Warmify.infra.SecurityConfigurations;
import com.Guilherme.Warmify.repositories.BusinessPortfolioRepository;
import com.Guilherme.Warmify.repositories.DomainRepository;
import com.Guilherme.Warmify.repositories.FacebookRepository;
import com.Guilherme.Warmify.repositories.InstagramRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/business-portfolios")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class BusinessPortfolioController {

    @Autowired
    private BusinessPortfolioRepository businessPortfolioRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private FacebookRepository facebookRepository;

    @Autowired
    private InstagramRepository instagramRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<BusinessPortfolioResponseDTO> portfolios = businessPortfolioRepository.findAll().stream()
                .map(BusinessPortfolioResponseDTO::new)
                .toList();

        return ResponseEntity.ok(portfolios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        BusinessPortfolio portfolio = businessPortfolioRepository.findById(id).orElse(null);

        if (portfolio == null) {
            return ResponseEntity.status(404).body("Portfolio não encontrado.");
        }

        return ResponseEntity.ok(new BusinessPortfolioResponseDTO(portfolio));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid BusinessPortfolioRegisterDTO data) {
        Domain domain = data.domainId() != null ? domainRepository.findById(data.domainId()).orElse(null) : null;
        Facebook facebook = facebookRepository.findById(data.facebookAccountId()).orElse(null);
        Instagram instagram = data.instagramAccountId() != null ? instagramRepository.findById(data.instagramAccountId()).orElse(null) : null;

        if (facebook == null) {
            return ResponseEntity.status(404).body("Conta Facebook não encontrada.");
        }

        if (data.domainId() != null && domain == null) {
            return ResponseEntity.status(404).body("Domínio não encontrado.");
        }

        if (data.instagramAccountId() != null && instagram == null) {
            return ResponseEntity.status(404).body("Conta Instagram não encontrada.");
        }

        BusinessPortfolio portfolio = new BusinessPortfolio(
                data.bmName(),
                data.cnpj(),
                data.cnpjPdf(),
                data.status(),
                domain,
                facebook,
                instagram
        );

        businessPortfolioRepository.save(portfolio);
        return ResponseEntity.status(201).body(new BusinessPortfolioResponseDTO(portfolio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody @Valid BusinessPortfolioRegisterDTO data) {
        BusinessPortfolio portfolio = businessPortfolioRepository.findById(id).orElse(null);

        if (portfolio == null) {
            return ResponseEntity.status(404).body("Portfolio não encontrado.");
        }

        Domain domain = data.domainId() != null ? domainRepository.findById(data.domainId()).orElse(null) : null;
        Facebook facebook = facebookRepository.findById(data.facebookAccountId()).orElse(null);
        Instagram instagram = data.instagramAccountId() != null ? instagramRepository.findById(data.instagramAccountId()).orElse(null) : null;

        if (facebook == null) {
            return ResponseEntity.status(404).body("Conta Facebook não encontrada.");
        }

        if (data.domainId() != null && domain == null) {
            return ResponseEntity.status(404).body("Domínio não encontrado.");
        }

        if (data.instagramAccountId() != null && instagram == null) {
            return ResponseEntity.status(404).body("Conta Instagram não encontrada.");
        }

        portfolio.setBmName(data.bmName());
        portfolio.setCnpj(data.cnpj());
        portfolio.setCnpjPdf(data.cnpjPdf());
        portfolio.setStatus(data.status());
        portfolio.setDomain(domain);
        portfolio.setFacebookAccount(facebook);
        portfolio.setInstagramAccount(instagram);

        businessPortfolioRepository.save(portfolio);
        return ResponseEntity.ok(new BusinessPortfolioResponseDTO(portfolio));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!businessPortfolioRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Portfolio não encontrado.");
        }

        businessPortfolioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


