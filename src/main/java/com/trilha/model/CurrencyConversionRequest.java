package com.trilha.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyConversionRequest {
    private String from;
    private String to;
    private Double valor;
}
