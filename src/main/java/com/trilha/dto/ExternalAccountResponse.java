package com.trilha.dto;

import lombok.Data;

@Data
public class ExternalAccountResponse {
    private String accountNumber;
    private Double balance;
    private String userId;
}
