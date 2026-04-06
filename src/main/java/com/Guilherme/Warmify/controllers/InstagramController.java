package com.Guilherme.Warmify.controllers;

import com.Guilherme.Warmify.domain.instagram.Instagram;
import com.Guilherme.Warmify.domain.instagram.dto.InstagramRegisterDTO;
import com.Guilherme.Warmify.domain.instagram.dto.InstagramResponseDTO;
import com.Guilherme.Warmify.infra.SecurityConfigurations;
import com.Guilherme.Warmify.repositories.InstagramRepository;
import com.Guilherme.Warmify.utils.StatusAccount;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("instagram")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class InstagramController {

    @Autowired
    private InstagramRepository instagramRepository;

    @GetMapping("/accounts")
    public ResponseEntity<?> getAllAccounts() {

        List<InstagramResponseDTO> accounts = instagramRepository
                .findAll()
                .stream()
                .map(InstagramResponseDTO::new)
                .toList();

        if(accounts.isEmpty()) {
            return ResponseEntity.status(404).body("Nenhuma conta encontrada");
        }

        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accountById")
    public ResponseEntity<?> getAccountById(@RequestParam UUID id) {

        List<InstagramResponseDTO> account = instagramRepository
                .findById(id)
                .stream()
                .map(InstagramResponseDTO::new)
                .toList();

        if(account.isEmpty()) {
            return ResponseEntity.status(404).body("Nenhuma conta encontrada.");
        }

        return ResponseEntity.ok(account);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerInstagramAccount(@RequestBody @Valid InstagramRegisterDTO data) {
        if(instagramRepository.existsByEmail(data.email())) {
            return ResponseEntity.badRequest().body("Conta já cadastrada.");
        }

        Instagram instagram = new Instagram(
                data.username(),
                data.email(),
                data.password(),
                data.googleAuthenticatorEmail(),
                StatusAccount.AVAILABLE
        );

        instagramRepository.save(instagram);

        return ResponseEntity.ok("Conta registrada com sucesso.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteInstagramAccount(@RequestParam UUID id) {

        if(!instagramRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Nenhuma conta encontrada.");
        }

        instagramRepository.deleteById(id);

        return ResponseEntity.ok("Instagram deletado com sucesso.");
    }

    @PutMapping("/edit")
    public ResponseEntity<String> editInstagramAccount(@RequestParam UUID id, @RequestBody @Valid InstagramRegisterDTO data) {
        Instagram instagram = instagramRepository.findById(id).orElse(null);

        if(instagram == null) {
            return ResponseEntity.status(404).body("Conta não encontrada");
        }

        instagram.setUsername(data.username());
        instagram.setEmail(data.email());
        instagram.setPassword(data.password());
        instagram.setGoogleAuthenticatorEmail(data.googleAuthenticatorEmail());

        instagramRepository.save(instagram);

        return ResponseEntity.ok("Alterações feitas com sucesso!");
    }
}
