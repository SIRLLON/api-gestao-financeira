package com.trilha.controller;

import com.trilha.config.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador responsável pela autenticação e geração de tokens JWT.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    /**
     * Construtor para injeção de dependência.
     *
     * @param jwtUtil Utilitário para geração de tokens JWT
     */
    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Gera um token JWT para autenticação.
     *
     * @return TokenResponse contendo o token JWT gerado
     */
    @Operation(summary = "Obter Token JWT", description = "Gera e retorna um token JWT válido para autenticação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token gerado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno ao gerar o token")
    })
    @GetMapping("/token")
    public ResponseEntity<TokenResponse> getToken() {
        String token = jwtUtil.generateToken("app-user");
        return ResponseEntity.ok(new TokenResponse(token));
    }
}
