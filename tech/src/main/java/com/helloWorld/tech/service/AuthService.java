package com.helloWorld.tech.service;

import com.helloWorld.tech.model.converter.UserConverter;
import com.helloWorld.tech.model.dao.UserDao;
import com.helloWorld.tech.model.dto.UserDto;
import com.helloWorld.tech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto login(String email, String rawPassword) {
        UserDao user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordMatches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        UserDto dto = UserConverter.toDTO(user);
        dto.setPassword(null); // never return password
        return dto;
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) return false;

        // Supports BCrypt hashes (recommended) and falls back to plain text (legacy).
        String normalized = storedPassword;
        if (normalized.startsWith("{bcrypt}")) {
            normalized = normalized.substring("{bcrypt}".length());
        }

        if (normalized.startsWith("$2a$") || normalized.startsWith("$2b$") || normalized.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, normalized);
        }
        return storedPassword.equals(rawPassword);
    }
}

