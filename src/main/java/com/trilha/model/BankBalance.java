package com.trilha.model;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class BankBalance {
    private String accountNumber;
    private Double balance;
    private String userId;
}
