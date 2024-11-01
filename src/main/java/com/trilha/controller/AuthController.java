package com.trilha.controller;

import com.trilha.config.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Endpoint para obter o token JWT
    @GetMapping("/token")
    public ResponseEntity<String> getToken() {
        String token = jwtUtil.generateToken("app-user");
        return ResponseEntity.ok(token);
    }
}
