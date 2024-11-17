package com.trilha.dto;

import com.trilha.model.Usuario;
import com.trilha.model.Categoria;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String descricao;
    private Double valor;
    private String data;
    private UsuarioResponse usuario;
    private Categoria categoria;
}
