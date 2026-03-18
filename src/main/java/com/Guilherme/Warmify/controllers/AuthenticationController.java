package com.Guilherme.Warmify.controllers;

import com.Guilherme.Warmify.domain.users.User;
import com.Guilherme.Warmify.domain.users.UserRole;
import com.Guilherme.Warmify.domain.users.dto.AuthenticationDTO;
import com.Guilherme.Warmify.domain.users.dto.LoginResponseDTO;
import com.Guilherme.Warmify.domain.users.dto.RegisterDTO;
import com.Guilherme.Warmify.infra.TokenService;
import com.Guilherme.Warmify.repositories.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Guilherme.Warmify.infra.SecurityConfigurations;

@RestController
@RequestMapping("auth")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        User user = (User) auth.getPrincipal();

        if (!user.isActive()) {
            return ResponseEntity.status(403).body("Usuário inativo.");
        }

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegisterDTO data) {
        if (this.userRepository.findByEmail(data.email()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.name(), data.email(), encryptedPassword, UserRole.USER, true);
        this.userRepository.save(newUser);

        return ResponseEntity.ok().build();
    }
}
