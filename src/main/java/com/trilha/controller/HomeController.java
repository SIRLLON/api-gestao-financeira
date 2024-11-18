package com.trilha.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador responsável por fornecer uma mensagem de boas-vindas.
 */
@RestController
@RequestMapping("/home")
public class HomeController {

    /**
     * Exibe uma mensagem de boas-vindas.
     *
     * @return String contendo a mensagem de boas-vindas
     */
    @Operation(summary = "Mensagem de boas-vindas", description = "Retorna uma mensagem simples de boas-vindas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensagem retornada com sucesso")
    })
    @GetMapping
    public String home() {
        return "Bem-vindo à aplicação do Sirllon!";
    }
}
