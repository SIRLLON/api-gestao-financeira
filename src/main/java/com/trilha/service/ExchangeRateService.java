package com.trilha.service;

import com.trilha.model.ExchangeRateResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ExchangeRateService {

    @Value("${exchange.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;


    public ExchangeRateResponse getExchangeRate(String from) {
        String url = "https://api.exchangeratesapi.io/v1/latest?access_key=" + apiKey + "&base=" + from;
        ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);

        if (response != null && response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException("Failed to retrieve exchange rates");
        }
    }

    public ConversionResult convertCurrency(String from, String to, Double amount) {
        ExchangeRateResponse response = getExchangeRate("EUR"); // EUR limitacao apifree
        Double fromRate = response.getRates().get(from);
        Double toRate = response.getRates().get(to);

        if (fromRate == null || toRate == null) {
            throw new RuntimeException("Exchange rate not available for " + from + " or " + to);
        }

        Double eurValue = amount / fromRate;
        Double convertedValue = eurValue * toRate;
        Double exchangeRate = toRate / fromRate; // Calcula a taxa de câmbio direta entre as moedas

        // Formatar os valores para no máximo duas casas decimais
        convertedValue = formatToTwoDecimalPlaces(convertedValue);
        exchangeRate = formatToTwoDecimalPlaces(exchangeRate);

        return new ConversionResult(convertedValue, exchangeRate);
    }

    // Método auxiliar para formatar valores
    private Double formatToTwoDecimalPlaces(Double value) {
        if (value == null) return null;
        return new BigDecimal(value)
                .setScale(2, RoundingMode.HALF_UP) // Arredondar para 2 casas decimais
                .doubleValue();
    }

    @Data
    @AllArgsConstructor
    public class ConversionResult {
        private Double convertedValue;
        private Double exchangeRate;
    }
}
