package com.Guilherme.Warmify.controllers;

import com.Guilherme.Warmify.domain.domain.Domain;
import com.Guilherme.Warmify.domain.domain.dto.DomainRegisterDTO;
import com.Guilherme.Warmify.domain.domain.dto.DomainResponseDTO;
import com.Guilherme.Warmify.infra.SecurityConfigurations;
import com.Guilherme.Warmify.repositories.DomainRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("domain")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class DomainController {

    @Autowired
    private DomainRepository domainRepository;

    @GetMapping("/domains")
    public ResponseEntity<?> getAllDomains() {

        List<DomainResponseDTO> domainList = domainRepository
                .findAll()
                .stream()
                .map(DomainResponseDTO::new)
                .toList();

        if(domainList.isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum domínio encontrado");
        }

        return ResponseEntity.ok(domainList);
    }

    @GetMapping("/domainById")
    public ResponseEntity<?> getDomainsById(@RequestParam UUID id) {

        Domain domain = domainRepository.findById(id).orElse(null);

        if(domain == null) {
            return ResponseEntity.status(404).body("Nenhum domínio encontrado");
        }

        return ResponseEntity.ok(domain);
    }

    @GetMapping("/domainByUrl")
    public ResponseEntity<?> getDomainsById(@RequestParam String url) {

        Domain domain = domainRepository.findByDomUrl(url);

        if(domain == null) {
            return ResponseEntity.status(404).body("Nenhum domínio encontrado");
        }

        return ResponseEntity.ok(domain);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerDomain(@RequestBody @Valid DomainRegisterDTO data) {

        if(domainRepository.existsByDomUrl(data.domUrl())) {
            return ResponseEntity.badRequest().body("Já existe um cadastro para este domínio.");
        }

        Domain newDomain = new Domain(data.domUrl());
        domainRepository.save(newDomain);
        return ResponseEntity.ok("Domínio cadastrado com sucesso.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteDomain(@RequestParam String domUrl) {

        if(!domainRepository.existsByDomUrl(domUrl)) {
            return ResponseEntity.badRequest().body("Domínio não encontrado.");
        }

        domainRepository.deleteByDomUrl(domUrl);

        return ResponseEntity.ok("Domínio deletado com sucesso.");
    }

    @PutMapping("/edit")
    public ResponseEntity<String> alterDomain(@RequestParam String domUrl, @RequestParam String newDom) {
        Domain domain = domainRepository.findByDomUrl(domUrl);

        if (domain == null) {
            return ResponseEntity.badRequest().body("Domínio não encontrado.");
        }

        domain.setDomUrl(newDom);
        domainRepository.save(domain);

        return ResponseEntity.ok("Domínio alterado com sucesso!");
    }
}
