package com.trilha.dto;

import com.trilha.model.Categoria;
import com.trilha.model.Usuario;
import lombok.Data;

@Data
public class TransactionRequest {
    private String descricao;
    private Double valor;
    private String data;
    private Usuario usuarioId;
    private Categoria categoriaId;
    private String from;
    private String to;
}

