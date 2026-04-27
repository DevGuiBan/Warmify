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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        return ResponseEntity.ok(facebookPageRepository
                .findAll()
                .stream()
                .map(page -> new FacebookPageResponse(page, findPortfolioIdByPageId(page.getId())))
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FacebookPage page = facebookPageRepository.findById(id).orElse(null);

        if (page == null) {
            return ResponseEntity.status(404).body("Página não encontrada.");
        }

        return ResponseEntity.ok(new FacebookPageResponse(page, findPortfolioIdByPageId(page.getId())));
    }

    @GetMapping("/by-portfolio/{portfolioId}")
    public ResponseEntity<?> getByPortfolio(@PathVariable UUID portfolioId) {

        BusinessPortfolio portfolio = businessPortfolioRepository.findById(portfolioId).orElse(null);

        if (portfolio == null) {
            return ResponseEntity.status(404).body("Portfolio não encontrado.");
        }

        List<UUID> pageIds = portfolio.getFacebookPages() == null ? List.of() : portfolio.getFacebookPages();

        List<FacebookPageResponse> pages = facebookPageRepository.findAllById(pageIds)
                .stream()
                .map(page -> new FacebookPageResponse(page, portfolioId))
                .toList();
        return ResponseEntity.ok(pages);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody FacebookPageRequest data) {
        Facebook facebook = facebookRepository.findById(data.facebookAccountId()).orElse(null);
        BusinessPortfolio portfolio = businessPortfolioRepository.findById(data.businessPortfolioId()).orElse(null);

        if (facebook == null) {
            return ResponseEntity.status(404).body("Conta Facebook não encontrada.");
        }
        if (portfolio == null) {
            return ResponseEntity.status(404).body("Portfolio não encontrado.");
        }

        FacebookPage page = new FacebookPage(data.pageName(), facebook);
        facebookPageRepository.save(page);

        linkPageToPortfolio(portfolio, page.getId());
        businessPortfolioRepository.save(portfolio);

        return ResponseEntity.status(201).body(new FacebookPageResponse(page, portfolio.getId()));
    }

    @PutMapping("/{id}")
    @Transactional
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

        UUID previousPortfolioId = findPortfolioIdByPageId(page.getId());
        page.setPageName(data.pageName());
        page.setFacebookAccount(facebook);
        facebookPageRepository.save(page);

        if (previousPortfolioId != null && !previousPortfolioId.equals(portfolio.getId())) {
            unlinkPageFromPortfolio(previousPortfolioId, page.getId());
        }
        linkPageToPortfolio(portfolio, page.getId());
        businessPortfolioRepository.save(portfolio);

        return ResponseEntity.ok(new FacebookPageResponse(page, portfolio.getId()));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        FacebookPage page = facebookPageRepository.findById(id).orElse(null);
        if (page == null) {
            return ResponseEntity.status(404).body("Página não encontrada.");
        }

        UUID portfolioId = findPortfolioIdByPageId(id);
        if (portfolioId != null) {
            unlinkPageFromPortfolio(portfolioId, id);
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

        public FacebookPageResponse(FacebookPage page, UUID businessPortfolioId) {
            this(page.getId(), page.getPageName(), page.getFacebookAccount().getId(), businessPortfolioId);
        }
    }

    private void linkPageToPortfolio(BusinessPortfolio portfolio, UUID pageId) {
        List<UUID> pageIds = portfolio.getFacebookPages() == null ? new ArrayList<>() : new ArrayList<>(portfolio.getFacebookPages());
        if (!pageIds.contains(pageId)) {
            pageIds.add(pageId);
        }
        portfolio.setFacebookPages(pageIds);
    }

    private void unlinkPageFromPortfolio(UUID portfolioId, UUID pageId) {
        BusinessPortfolio portfolio = businessPortfolioRepository.findById(portfolioId).orElse(null);
        if (portfolio == null || portfolio.getFacebookPages() == null) {
            return;
        }

        List<UUID> pageIds = new ArrayList<>(portfolio.getFacebookPages());
        pageIds.remove(pageId);
        portfolio.setFacebookPages(pageIds);
        businessPortfolioRepository.save(portfolio);
    }

    private UUID findPortfolioIdByPageId(UUID pageId) {
        return businessPortfolioRepository.findAll().stream()
                .filter(portfolio -> portfolio.getFacebookPages() != null && portfolio.getFacebookPages().contains(pageId))
                .map(BusinessPortfolio::getId)
                .findFirst()
                .orElse(null);
    }
}


