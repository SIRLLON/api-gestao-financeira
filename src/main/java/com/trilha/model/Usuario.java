package com.trilha.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Entity
@Table(name = "users")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String nome;
    @Email(message = "Formato de e-mail inválido")
    private String email;
    @NotBlank
    @JsonIgnore
    private String senha;

    @NotNull(message = "O saldo não pode ser nulo.")
    private Double saldo; // Novo campo para armazenar o saldo

    @Column(nullable = true)
    private String accountNumber; // Novo campo para o número da conta

    public Usuario() {
        this.saldo = 0.0; // Inicializa o saldo com um valor padrão
    }


    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Usuario(String nome, String email, String senha, Double saldo) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.saldo = saldo;
    }
}