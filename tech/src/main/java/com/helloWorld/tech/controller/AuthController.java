package com.helloWorld.tech.controller;

import com.helloWorld.tech.model.dto.LoginRequest;
import com.helloWorld.tech.model.dto.LoginResponse;
import com.helloWorld.tech.model.dto.UserDto;
import com.helloWorld.tech.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            UserDto user = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(new LoginResponse(true, "Login successful", user));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, ex.getMessage(), null));
        }
    }
}

