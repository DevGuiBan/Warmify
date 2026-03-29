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

@RestController
@RequestMapping("domain")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class DomainController {

    @Autowired
    private DomainRepository domainRepository;

    @GetMapping("/domains")
    public ResponseEntity<?> getAllDomains() {
        try {
            List<DomainResponseDTO> domainList = domainRepository.findAll().stream().map(DomainResponseDTO::new).toList();

            return ResponseEntity.ok(domainList);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerDomain(@RequestBody @Valid DomainRegisterDTO data) {
        if(this.domainRepository.findByDomUrl(data.domUrl()) != null) return ResponseEntity.badRequest().build();

        Domain newDomain = new Domain(data.domUrl());
        domainRepository.save(newDomain);
        return ResponseEntity.ok("Domínio cadastrado com sucesso.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteDomain(@RequestParam String domUrl) {
        Domain domain = domainRepository.findByDomUrl(domUrl);

        if(domain == null) return ResponseEntity.notFound().build();

        try {
            domainRepository.delete(domain);
            return ResponseEntity.ok("Domínio deletado com sucesso.");
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    @PutMapping("/edit")
    public ResponseEntity<?> alterDomain(@RequestParam String domUrl, @RequestParam String newDom) {
        Domain domain = domainRepository.findByDomUrl(domUrl);

        if (domain == null) return ResponseEntity.notFound().build();

        try {
            domain.setDomUrl(newDom);
            domainRepository.save(domain);
            return ResponseEntity.ok("Domínio alterado com sucesso!");
        }catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
