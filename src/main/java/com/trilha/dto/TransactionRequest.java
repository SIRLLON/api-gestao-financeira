package com.trilha.dto;

import com.trilha.model.Categoria;
import com.trilha.model.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionRequest {
    @NotBlank(message = "A descrição é obrigatória.")
    private String descricao;

    @NotNull(message = "O valor é obrigatório.")
    @Positive(message = "O valor deve ser positivo.")
    private Double valor;

    @NotNull(message = "A data é obrigatória.")
    @PastOrPresent(message = "A data deve ser no passado ou presente.")
    private LocalDate data;

    @NotNull(message = "O ID do usuário é obrigatório.")
    private Long usuarioId;

    @NotNull(message = "O ID da categoria é obrigatório.")
    private Long categoriaId;
    @NotBlank(message = "O código da moeda de origem é obrigatório.")
    private String from;
    @NotBlank(message = "O código da moeda de destino é obrigatório.")
    private String to;
}

