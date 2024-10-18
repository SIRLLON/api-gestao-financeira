package com.trilha.service;

import com.trilha.model.CurrencyConversionResult;
import com.trilha.model.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateService {

    @Value("${exchange.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;


    public Double getCurrentExchangeRate(String from, String to) {
        ExchangeRateResponse response = getExchangeRate("EUR"); // Continuamos utilizando EUR como base
        Double fromRate = response.getRates().get(from);
        Double toRate = response.getRates().get(to);

        if (fromRate == null || toRate == null) {
            throw new RuntimeException("Exchange rate not available for " + from + " or " + to);
        }

        // Calcula a taxa de câmbio entre as duas moedas
        return toRate / fromRate;
    }
    public ExchangeRateResponse getExchangeRate(String from) {
        String url = "https://api.exchangeratesapi.io/v1/latest?access_key=" + apiKey + "&base=" + from;
        ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);

        if (response != null && response.isSuccess()) {
            return response;
        } else {
            throw new RuntimeException("Failed to retrieve exchange rates");
        }
    }

//    public Double convertCurrency(String from, String to, Double amount) {
//        ExchangeRateResponse response = getExchangeRate("EUR"); // EUR limitacao apifree
//        Double fromRate = response.getRates().get(from);
//        Double toRate = response.getRates().get(to);
//
//        if (fromRate == null || toRate == null) {
//            throw new RuntimeException("Exchange rate not available for " + from + " or " + to);
//        }
//
//        // amount / fromRate = quanto valeria em EUR. *toRate para obter o valor final na moeda to
//        Double eurValue = amount / fromRate;
//        return eurValue * toRate;
//    }
    public CurrencyConversionResult convertCurrency(String from, String to, Double amount) {
        // taxa de câmbio atual usando o método getCurrentExchangeRate
        Double exchangeRate = getCurrentExchangeRate(from, to);

        // valor convertido com base na taxa de câmbio
        Double convertedAmount = amount * exchangeRate;

        // Retornamos o resultado com o valor convertido e a taxa de câmbio
        return new CurrencyConversionResult(convertedAmount, exchangeRate);
    }
}
