package com.trilha.model;
import lombok.Data;
import java.util.Map;

@Data
public class ExchangeRateResponse {
    private boolean success;
    private String base;
    private String date;
    private Map<String, Double> rates;
}
