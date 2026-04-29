package com.helloWorld.tech.service.impl;

import com.helloWorld.tech.model.dao.RefreshTokenDao;
import com.helloWorld.tech.model.dao.UserDao;
import com.helloWorld.tech.model.dto.TokenResponse;
import com.helloWorld.tech.repository.RefreshTokenRepository;
import com.helloWorld.tech.repository.UserRepository;
import com.helloWorld.tech.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtServiceImpl jwtService;

    @Value("${jwt.refresh-token-days:7}")
    private long refreshTokenDays;

    @Override
    public TokenResponse login(String email, String rawPassword) {
        UserDao user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordMatches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        List<String> roles = userRepository.findRoleByUserId(user.getId())
                .filter(r -> !r.isBlank())
                .map(String::trim)
                .map(List::of)
                .orElse(List.of());
        String accessToken = jwtService.createAccessToken(user.getId(), user.getEmail(), roles);

        String refreshToken = generateRefreshToken();
        Instant now = Instant.now();
        Instant refreshExp = now.plusSeconds(refreshTokenDays * 24 * 60 * 60);

        RefreshTokenDao rt = new RefreshTokenDao();
        rt.setUserId(user.getId());
        rt.setTokenHash(sha256Hex(refreshToken));
        rt.setExpiresAt(refreshExp);
        rt.setRevoked(false);
        rt.setCreatedAt(now);
        refreshTokenRepository.save(rt);

        long accessExpiresIn = 15 * 60; // keep aligned with jwt.access-token-minutes default
        return new TokenResponse(
                "Bearer",
                accessToken,
                accessExpiresIn,
                refreshToken,
                refreshTokenDays * 24 * 60 * 60
        );
    }

    @Override
    public TokenResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        RefreshTokenDao existing = refreshTokenRepository.findByTokenHash(sha256Hex(refreshToken))
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (existing.isRevoked() || existing.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        UserDao user = userRepository.findById(existing.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        // Rotate refresh token: revoke old, issue new.
        existing.setRevoked(true);
        refreshTokenRepository.save(existing);

        String newRefresh = generateRefreshToken();
        Instant now = Instant.now();
        Instant refreshExp = now.plusSeconds(refreshTokenDays * 24 * 60 * 60);

        RefreshTokenDao rt = new RefreshTokenDao();
        rt.setUserId(user.getId());
        rt.setTokenHash(sha256Hex(newRefresh));
        rt.setExpiresAt(refreshExp);
        rt.setRevoked(false);
        rt.setCreatedAt(now);
        refreshTokenRepository.save(rt);

        List<String> roles = userRepository.findRoleByUserId(user.getId())
                .filter(r -> !r.isBlank())
                .map(String::trim)
                .map(List::of)
                .orElse(List.of());
        String newAccess = jwtService.createAccessToken(user.getId(), user.getEmail(), roles);
        long accessExpiresIn = 15 * 60;
        return new TokenResponse(
                "Bearer",
                newAccess,
                accessExpiresIn,
                newRefresh,
                refreshTokenDays * 24 * 60 * 60
        );
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

    private static String generateRefreshToken() {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String sha256Hex(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(dig.length * 2);
            for (byte b : dig) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash token", e);
        }
    }
}

