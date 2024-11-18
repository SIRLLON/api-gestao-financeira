package com.trilha.dto;

import lombok.Data;


import java.util.Locale;
import java.text.NumberFormat;

@Data
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

    private String formatBalance(Double balance) {
        Locale brazil = new Locale("pt", "BR");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(brazil);
        return currencyFormatter.format(balance);
    }

    private String formatAccountNumber(String accountNumber) {
        if (accountNumber.length() == 8) {
            return accountNumber.substring(0, 3) + " " + accountNumber.substring(3, 7) + "-" + accountNumber.substring(7);
        }
        return accountNumber;
    }

}
