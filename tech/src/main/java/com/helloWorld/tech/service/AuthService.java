package com.helloWorld.tech.service;

import com.helloWorld.tech.model.dto.TokenResponse;

public interface AuthService {

    TokenResponse login(String email, String rawPassword);

    TokenResponse refresh(String refreshToken);
}
