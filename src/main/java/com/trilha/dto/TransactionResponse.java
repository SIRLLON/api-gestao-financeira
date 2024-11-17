package com.trilha.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.trilha.model.Usuario;
import com.trilha.model.Categoria;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {
    private Long id;
    private String descricao;
    private Double valorOriginal;
    private String data;
    private Double convertedValue;
    private Double exchangeRate;
    private UsuarioResponse usuario;
    private Categoria categoria;
}
