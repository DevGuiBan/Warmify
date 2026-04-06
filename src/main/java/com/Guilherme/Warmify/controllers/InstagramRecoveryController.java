package com.Guilherme.Warmify.controllers;

import com.Guilherme.Warmify.domain.instagram.Instagram;
import com.Guilherme.Warmify.domain.recoveryKeys.InstagramRecovery;
import com.Guilherme.Warmify.infra.SecurityConfigurations;
import com.Guilherme.Warmify.repositories.InstagramRecoveryRepository;
import com.Guilherme.Warmify.repositories.InstagramRepository;
import com.Guilherme.Warmify.utils.RecoveryKeyStatus;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/instagram-recovery-keys")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class InstagramRecoveryController {

    @Autowired
    private InstagramRecoveryRepository instagramRecoveryRepository;

    @Autowired
    private InstagramRepository instagramRepository;

    @GetMapping("/by-account/{instagramAccountId}")
    public ResponseEntity<?> getByAccount(@PathVariable UUID instagramAccountId) {
        if (!instagramRepository.existsById(instagramAccountId)) {
            return ResponseEntity.status(404).body("Conta Instagram não encontrada.");
        }

        List<InstagramRecoveryResponse> keys = instagramRecoveryRepository.findByInstagramAccountId(instagramAccountId)
                .stream()
                .map(InstagramRecoveryResponse::new)
                .toList();
        return ResponseEntity.ok(keys);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody InstagramRecoveryRequest data) {
        Instagram account = instagramRepository.findById(data.instagramAccountId()).orElse(null);
        if (account == null) {
            return ResponseEntity.status(404).body("Conta Instagram não encontrada.");
        }

        InstagramRecovery recovery = new InstagramRecovery(data.recoveryKey(), data.status(), account);
        instagramRecoveryRepository.save(recovery);
        return ResponseEntity.status(201).body(new InstagramRecoveryResponse(recovery));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody InstagramRecoveryRequest data) {
        InstagramRecovery recovery = instagramRecoveryRepository.findById(id).orElse(null);
        if (recovery == null) {
            return ResponseEntity.status(404).body("Chave não encontrada.");
        }

        Instagram account = instagramRepository.findById(data.instagramAccountId()).orElse(null);
        if (account == null) {
            return ResponseEntity.status(404).body("Conta Instagram não encontrada.");
        }

        recovery.setRecoveryKey(data.recoveryKey());
        recovery.setStatus(data.status());
        recovery.setInstagramAccount(account);
        instagramRecoveryRepository.save(recovery);
        return ResponseEntity.ok(new InstagramRecoveryResponse(recovery));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable UUID id, @RequestParam RecoveryKeyStatus status) {
        InstagramRecovery recovery = instagramRecoveryRepository.findById(id).orElse(null);
        if (recovery == null) {
            return ResponseEntity.status(404).body("Chave não encontrada.");
        }

        recovery.setStatus(status);
        instagramRecoveryRepository.save(recovery);
        return ResponseEntity.ok(new InstagramRecoveryResponse(recovery));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!instagramRecoveryRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Chave não encontrada.");
        }
        instagramRecoveryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record InstagramRecoveryRequest(
            @NotBlank String recoveryKey,
            @NotNull RecoveryKeyStatus status,
            @NotNull UUID instagramAccountId) {
    }

    public record InstagramRecoveryResponse(
            UUID id,
            String recoveryKey,
            RecoveryKeyStatus status,
            UUID instagramAccountId) {

        public InstagramRecoveryResponse(InstagramRecovery recovery) {
            this(recovery.getId(), recovery.getRecoveryKey(), recovery.getStatus(), recovery.getInstagramAccount().getId());
        }
    }
}

