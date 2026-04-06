package com.Guilherme.Warmify.controllers;

import com.Guilherme.Warmify.domain.businessportfolio.BusinessPortfolio;
import com.Guilherme.Warmify.domain.facebook.Facebook;
import com.Guilherme.Warmify.domain.facebookpage.FacebookPage;
import com.Guilherme.Warmify.infra.SecurityConfigurations;
import com.Guilherme.Warmify.repositories.BusinessPortfolioRepository;
import com.Guilherme.Warmify.repositories.FacebookPageRepository;
import com.Guilherme.Warmify.repositories.FacebookRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/facebook-pages")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class FacebookPageController {

    @Autowired
    private FacebookPageRepository facebookPageRepository;

    @Autowired
    private FacebookRepository facebookRepository;

    @Autowired
    private BusinessPortfolioRepository businessPortfolioRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(facebookPageRepository.findAll().stream().map(FacebookPageResponse::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FacebookPage page = facebookPageRepository.findById(id).orElse(null);
        if (page == null) {
            return ResponseEntity.status(404).body("Página não encontrada.");
        }

        return ResponseEntity.ok(new FacebookPageResponse(page));
    }

    @GetMapping("/by-portfolio/{portfolioId}")
    public ResponseEntity<?> getByPortfolio(@PathVariable UUID portfolioId) {
        if (!businessPortfolioRepository.existsById(portfolioId)) {
            return ResponseEntity.status(404).body("Portfolio não encontrado.");
        }

        List<FacebookPageResponse> pages = facebookPageRepository.findByBusinessPortfolioId(portfolioId)
                .stream()
                .map(FacebookPageResponse::new)
                .toList();
        return ResponseEntity.ok(pages);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody FacebookPageRequest data) {
        Facebook facebook = facebookRepository.findById(data.facebookAccountId()).orElse(null);
        BusinessPortfolio portfolio = businessPortfolioRepository.findById(data.businessPortfolioId()).orElse(null);

        if (facebook == null) {
            return ResponseEntity.status(404).body("Conta Facebook não encontrada.");
        }
        if (portfolio == null) {
            return ResponseEntity.status(404).body("Portfolio não encontrado.");
        }

        FacebookPage page = new FacebookPage(data.pageName(), facebook, portfolio);
        facebookPageRepository.save(page);
        return ResponseEntity.status(201).body(new FacebookPageResponse(page));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody FacebookPageRequest data) {
        FacebookPage page = facebookPageRepository.findById(id).orElse(null);
        if (page == null) {
            return ResponseEntity.status(404).body("Página não encontrada.");
        }

        Facebook facebook = facebookRepository.findById(data.facebookAccountId()).orElse(null);
        BusinessPortfolio portfolio = businessPortfolioRepository.findById(data.businessPortfolioId()).orElse(null);

        if (facebook == null) {
            return ResponseEntity.status(404).body("Conta Facebook não encontrada.");
        }
        if (portfolio == null) {
            return ResponseEntity.status(404).body("Portfolio não encontrado.");
        }

        page.setPageName(data.pageName());
        page.setFacebookAccount(facebook);
        page.setBusinessPortfolio(portfolio);
        facebookPageRepository.save(page);
        return ResponseEntity.ok(new FacebookPageResponse(page));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!facebookPageRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Página não encontrada.");
        }
        facebookPageRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record FacebookPageRequest(
            @NotBlank String pageName,
            @NotNull UUID facebookAccountId,
            @NotNull UUID businessPortfolioId) {
    }

    public record FacebookPageResponse(
            UUID id,
            String pageName,
            UUID facebookAccountId,
            UUID businessPortfolioId) {

        public FacebookPageResponse(FacebookPage page) {
            this(page.getId(), page.getPageName(), page.getFacebookAccount().getId(), page.getBusinessPortfolio().getId());
        }
    }
}


