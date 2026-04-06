package com.Guilherme.Warmify.controllers;

import com.Guilherme.Warmify.domain.facebook.Facebook;
import com.Guilherme.Warmify.domain.facebook.dto.FacebookRegisterDTO;
import com.Guilherme.Warmify.domain.facebook.dto.FacebookResponseDTO;
import com.Guilherme.Warmify.infra.SecurityConfigurations;
import com.Guilherme.Warmify.repositories.FacebookRepository;
import com.Guilherme.Warmify.utils.StatusAccount;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("facebook")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class FacebookController {

    @Autowired
    private FacebookRepository facebookRepository;

    @GetMapping("/accounts")
    public ResponseEntity<?> getAllAccounts() {

        List<FacebookResponseDTO> accounts = facebookRepository
                .findAll()
                .stream()
                .map(FacebookResponseDTO::new)
                .toList();

        if(accounts.isEmpty()) {
            return ResponseEntity.status(404).body("Nenhuma conta encontrada.");
        }

        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accountById")
    public ResponseEntity<?> accountById(@RequestParam UUID id) {

        List<FacebookResponseDTO> account = facebookRepository
                .findById(id)
                .stream()
                .map(FacebookResponseDTO::new)
                .toList();

        if(account.isEmpty()) {
            return ResponseEntity.status(404).body("Nenhuma conta encontrada.");
        }

        return  ResponseEntity.ok(account);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerFacebookAccount(@RequestBody @Valid FacebookRegisterDTO data) {

        if(facebookRepository.existsByEmail(data.email())) {
            return ResponseEntity.badRequest().body("Conta já cadastrada");
        }

        Facebook facebook = new Facebook(
                data.profileName(),
                data.email(),
                data.password(),
                data.url(),
                StatusAccount.AVAILABLE);

        facebookRepository.save(facebook);

        return ResponseEntity.ok("Conta registrada com sucesso.");
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editFacebookAccount(@RequestParam UUID id, @RequestBody @Valid FacebookRegisterDTO data) {

        Facebook facebook = facebookRepository.findById(id).orElse(null);

        if(facebook == null) {
            return ResponseEntity.badRequest().body("Conta não encontrada.");
        }

        facebook.setProfileName(data.profileName());
        facebook.setEmail(data.email());
        facebook.setPassword(data.password());
        facebook.setUrl(data.url());

        facebookRepository.save(facebook);

        return ResponseEntity.ok("Alterações feitas com sucesso.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFacebookAccount(@RequestParam UUID id) {

        if(!facebookRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Conta não encontrada.");
        }

        facebookRepository.deleteById(id);

        return ResponseEntity.ok("Conta deletada com sucesso.");
    }

}
