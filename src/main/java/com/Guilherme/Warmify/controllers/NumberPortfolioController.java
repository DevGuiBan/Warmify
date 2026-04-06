package com.Guilherme.Warmify.controllers;

import com.Guilherme.Warmify.domain.businessportfolio.BusinessPortfolio;
import com.Guilherme.Warmify.domain.numberportfolio.NumberPortfolio;
import com.Guilherme.Warmify.infra.SecurityConfigurations;
import com.Guilherme.Warmify.repositories.BusinessPortfolioRepository;
import com.Guilherme.Warmify.repositories.NumberPortfolioRepository;
import com.Guilherme.Warmify.utils.StatusAccount;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/number-portfolios")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class NumberPortfolioController {

    @Autowired
    private NumberPortfolioRepository numberPortfolioRepository;

    @Autowired
    private BusinessPortfolioRepository businessPortfolioRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(numberPortfolioRepository.findAll().stream().map(NumberPortfolioResponse::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        NumberPortfolio number = numberPortfolioRepository.findById(id).orElse(null);
        if (number == null) {
            return ResponseEntity.status(404).body("Número não encontrado.");
        }

        return ResponseEntity.ok(new NumberPortfolioResponse(number));
    }

    @GetMapping("/by-portfolio/{portfolioId}")
    public ResponseEntity<?> getByPortfolio(@PathVariable UUID portfolioId) {
        if (!businessPortfolioRepository.existsById(portfolioId)) {
            return ResponseEntity.status(404).body("Portfolio não encontrado.");
        }

        List<NumberPortfolioResponse> numbers = numberPortfolioRepository.findByBusinessPortfolioId(portfolioId)
                .stream()
                .map(NumberPortfolioResponse::new)
                .toList();
        return ResponseEntity.ok(numbers);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NumberPortfolioRequest data) {
        BusinessPortfolio portfolio = businessPortfolioRepository.findById(data.businessPortfolioId()).orElse(null);
        if (portfolio == null) {
            return ResponseEntity.status(404).body("Portfolio não encontrado.");
        }

        NumberPortfolio number = new NumberPortfolio(data.name(), data.number(), data.status(), portfolio);
        numberPortfolioRepository.save(number);
        return ResponseEntity.status(201).body(new NumberPortfolioResponse(number));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody NumberPortfolioRequest data) {
        NumberPortfolio number = numberPortfolioRepository.findById(id).orElse(null);
        if (number == null) {
            return ResponseEntity.status(404).body("Número não encontrado.");
        }

        BusinessPortfolio portfolio = businessPortfolioRepository.findById(data.businessPortfolioId()).orElse(null);
        if (portfolio == null) {
            return ResponseEntity.status(404).body("Portfolio não encontrado.");
        }

        number.setName(data.name());
        number.setNumber(data.number());
        number.setStatus(data.status());
        number.setBusinessPortfolio(portfolio);
        numberPortfolioRepository.save(number);
        return ResponseEntity.ok(new NumberPortfolioResponse(number));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable UUID id, @RequestParam StatusAccount status) {
        NumberPortfolio number = numberPortfolioRepository.findById(id).orElse(null);
        if (number == null) {
            return ResponseEntity.status(404).body("Número não encontrado.");
        }

        number.setStatus(status);
        numberPortfolioRepository.save(number);
        return ResponseEntity.ok(new NumberPortfolioResponse(number));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!numberPortfolioRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Número não encontrado.");
        }
        numberPortfolioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record NumberPortfolioRequest(
            @NotBlank String name,
            @NotBlank String number,
            @NotNull StatusAccount status,
            @NotNull UUID businessPortfolioId) {
    }

    public record NumberPortfolioResponse(
            UUID id,
            String name,
            String number,
            StatusAccount status,
            UUID businessPortfolioId) {

        public NumberPortfolioResponse(NumberPortfolio number) {
            this(number.getId(), number.getName(), number.getNumber(), number.getStatus(), number.getBusinessPortfolio().getId());
        }
    }
}


