package com.helloWorld.tech.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String tokenType;
    private String accessToken;
    private long accessTokenExpiresInSeconds;
    private String refreshToken;
    private long refreshTokenExpiresInSeconds;
}

