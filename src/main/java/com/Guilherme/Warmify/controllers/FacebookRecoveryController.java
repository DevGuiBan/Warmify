package com.Guilherme.Warmify.controllers;

import com.Guilherme.Warmify.domain.facebook.Facebook;
import com.Guilherme.Warmify.domain.recoveryKeys.FacebookRecovery;
import com.Guilherme.Warmify.infra.SecurityConfigurations;
import com.Guilherme.Warmify.repositories.FacebookRecoveryRepository;
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
@RequestMapping("/facebook-recovery-keys")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class FacebookRecoveryController {

    @Autowired
    private FacebookRecoveryRepository facebookRecoveryRepository;

    @Autowired
    private FacebookRepository facebookRepository;

    @GetMapping("/by-account/{facebookAccountId}")
    public ResponseEntity<?> getByAccount(@PathVariable UUID facebookAccountId) {
        if (!facebookRepository.existsById(facebookAccountId)) {
            return ResponseEntity.status(404).body("Conta Facebook não encontrada.");
        }

        List<FacebookRecoveryResponse> keys = facebookRecoveryRepository.findByFacebookAccountId(facebookAccountId)
                .stream()
                .map(FacebookRecoveryResponse::new)
                .toList();
        return ResponseEntity.ok(keys);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody FacebookRecoveryRequest data) {
        Facebook account = facebookRepository.findById(data.facebookAccountId()).orElse(null);
        if (account == null) {
            return ResponseEntity.status(404).body("Conta Facebook não encontrada.");
        }

        FacebookRecovery recovery = new FacebookRecovery(data.recoveryKey(), account);
        facebookRecoveryRepository.save(recovery);
        return ResponseEntity.status(201).body(new FacebookRecoveryResponse(recovery));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody FacebookRecoveryRequest data) {
        FacebookRecovery recovery = facebookRecoveryRepository.findById(id).orElse(null);
        if (recovery == null) {
            return ResponseEntity.status(404).body("Chave não encontrada.");
        }

        Facebook account = facebookRepository.findById(data.facebookAccountId()).orElse(null);
        if (account == null) {
            return ResponseEntity.status(404).body("Conta Facebook não encontrada.");
        }

        recovery.setRecoveryKey(data.recoveryKey());
        recovery.setFacebookAccount(account);
        facebookRecoveryRepository.save(recovery);
        return ResponseEntity.ok(new FacebookRecoveryResponse(recovery));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!facebookRecoveryRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Chave não encontrada.");
        }
        facebookRecoveryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record FacebookRecoveryRequest(
            @NotBlank String recoveryKey,
            @NotNull UUID facebookAccountId) {
    }

    public record FacebookRecoveryResponse(
            UUID id,
            String recoveryKey,
            UUID facebookAccountId) {

        public FacebookRecoveryResponse(FacebookRecovery recovery) {
            this(recovery.getId(), recovery.getRecoveryKey(), recovery.getFacebookAccount().getId());
        }
    }
}

