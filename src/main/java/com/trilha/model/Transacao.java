package com.trilha.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "transacao") //nome da tabela no BD
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;
    private Double valor;
    private Double convertedValue; // Valor convertido
    private String origem;
    private String destino;
    private Double exchangeRate;
    @NotNull(message = "A data não pode ser nula.")
    @PastOrPresent(message = "A data deve ser do passado ou presente.")
    private LocalDate data;

    @ManyToOne
    @JoinColumn(name = "user_id") // chave primaria do Usuario
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;



    public Transacao() {
    }

    public Transacao(Long id, String descricao, Double valor, LocalDate data, Categoria categoria) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.categoria = categoria;
    }

    public Transacao(String descricao, Double valor, LocalDate data, Categoria categoria) {
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.categoria = categoria;
    }
}
