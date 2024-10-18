package com.trilha.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyConversionResult {
    private Double convertedAmount;
    private Double exchangeRate;
}
