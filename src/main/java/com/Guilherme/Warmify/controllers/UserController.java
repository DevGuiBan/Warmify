package com.Guilherme.Warmify.controllers;

import com.Guilherme.Warmify.domain.users.User;
import com.Guilherme.Warmify.domain.users.UserRole;
import com.Guilherme.Warmify.domain.users.dto.UserResponseDTO;
import com.Guilherme.Warmify.infra.SecurityConfigurations;
import com.Guilherme.Warmify.repositories.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("manager")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserResponseDTO> userList = userRepository.findAll().stream().map(UserResponseDTO::new).toList();

            return ResponseEntity.ok(userList);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/users/changeRole")
    public ResponseEntity<?> changeUserRole(@RequestParam String email, @RequestParam UserRole role) throws Exception {
        User user = userRepository.findUserByEmail(email).orElse(null);

        if (user == null) return ResponseEntity.notFound().build();

        try {
            user.setUserRole(role);
            userRepository.save(user);
            return ResponseEntity.ok("Alteração feita com sucesso.");
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @PutMapping("/users/deactivate")
    public ResponseEntity<?> userDeactivate(@RequestParam String email, @RequestParam boolean active) throws Exception {
        User user = userRepository.findUserByEmail(email).orElse(null);

        if (user == null) return ResponseEntity.notFound().build();

        try {
            user.setActive(active);
            userRepository.save(user);
            return ResponseEntity.ok("Alteração feita com sucesso.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
