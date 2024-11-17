package com.trilha.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
}
