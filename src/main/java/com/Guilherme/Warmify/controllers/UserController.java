package com.Guilherme.Warmify.controllers;

import com.Guilherme.Warmify.domain.users.User;
import com.Guilherme.Warmify.domain.users.dto.UserUpdateDTO;
import com.Guilherme.Warmify.utils.UserRole;
import com.Guilherme.Warmify.domain.users.dto.UserResponseDTO;
import com.Guilherme.Warmify.infra.SecurityConfigurations;
import com.Guilherme.Warmify.repositories.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("manager")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {

        List<UserResponseDTO> userList = userRepository.findAll().stream().map(UserResponseDTO::new).toList();

        if(userList.isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum usuário encontrado.");
        }

        return ResponseEntity.ok(userList);

    }

    @PutMapping("/changeRole")
    public ResponseEntity<String> changeUserRole(@RequestParam String email, @RequestParam UserRole role) {
        User user = userRepository.findUserByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Usuário não encontrado.");
        }

        user.setUserRole(role);
        userRepository.save(user);
        return ResponseEntity.ok("Alteração feita com sucesso.");
    }

    @PutMapping("/edit")
    public ResponseEntity<String> editUser(@RequestParam UUID id, @RequestBody UserUpdateDTO data) {
        User user = userRepository.findById(id).orElse(null);

        if(user == null) {
            return ResponseEntity.status(404).body("Usuário não encontrado.");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        user.setName(data.name());
        user.setEmail(data.email());
        user.setPassword(encryptedPassword);

        userRepository.save(user);

        return ResponseEntity.ok("Alterações feitas com sucesso!");
    }

    @PutMapping("/deactivate")
    public ResponseEntity<String> userDeactivate(@RequestParam String email, @RequestParam boolean active) {
        User user = userRepository.findUserByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Usuário não encontrado.");
        }

        user.setActive(active);
        userRepository.save(user);

        return ResponseEntity.ok("Alteração feita com sucesso.");
    }
}
