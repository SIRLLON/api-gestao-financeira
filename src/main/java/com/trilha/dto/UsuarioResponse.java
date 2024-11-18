package com.trilha.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;
import java.text.NumberFormat;

@Data
@Getter
@Setter
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    private String accountNumber;
    private String balance;

    public UsuarioResponse(Long id, String name, String email, String accountNumber, Double balance) {
        this.id = id;
        this.nome = name;
        this.email = email;
        this.accountNumber = formatAccountNumber(accountNumber);
        this.balance = formatBalance(balance);
    }

    // Métodos de formatação
    private String formatBalance(Double balance) {
        Locale brazil = new Locale("pt", "BR");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(brazil);
        return currencyFormatter.format(balance);
    }

    private String formatAccountNumber(String accountNumber) {
        if (accountNumber.length() == 8) {
            // Formatar conta com 8 dígitos: NNN NNNN-NN
            return accountNumber.substring(0, 3) + " " + accountNumber.substring(3, 7) + "-" + accountNumber.substring(7);
        }
        return accountNumber; // Retorna a conta como está se o formato não corresponder
    }

}
