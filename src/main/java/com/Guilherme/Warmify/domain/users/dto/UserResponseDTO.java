package com.Guilherme.Warmify.domain.users.dto;

import com.Guilherme.Warmify.domain.users.User;
import com.Guilherme.Warmify.domain.users.UserRole;

import java.util.UUID;

public record UserResponseDTO(UUID id, String name, String email, UserRole role, boolean active) {

    public UserResponseDTO(User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getUserRole(), user.isActive()
        );
    }
}
