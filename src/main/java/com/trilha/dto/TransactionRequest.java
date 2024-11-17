package com.trilha.dto;

import com.trilha.model.Categoria;
import com.trilha.model.Usuario;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionRequest {
    private String descricao;
    private Double valor;
    private LocalDate data;
    private Long usuarioId;
    private Long categoriaId;
    private String from;
    private String to;
}

