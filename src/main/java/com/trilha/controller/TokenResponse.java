package com.trilha.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Representação da resposta contendo o token JWT.
 */
@Data
@AllArgsConstructor
@Schema(description = "Resposta contendo o token JWT gerado para autenticação.")
public class TokenResponse {

    /**
     * Token JWT gerado.
     */
    @Schema(description = "Token JWT válido para autenticação.",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhcHAtdXNlciIsImlhdCI6MTY4MDgxMzUyNywiZXhwIjoxNjgwODEzNTI3fQ.Gt_4nCtuv-3eC6A9DItvE_dhxkX9uL-wbLO7o5lL5bU")
    private String token;
}
