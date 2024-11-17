package com.trilha.service;

import com.trilha.dto.ExternalAccountResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalAccountService {

    private final RestTemplate restTemplate;

    @Autowired
    public ExternalAccountService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ExternalAccountResponse getAccountData(Long userId) {
        String url = "https://66fada9c8583ac93b40a2cc3.mockapi.io/saldos/" + userId;
        ExternalAccountResponse response = restTemplate.getForObject(url, ExternalAccountResponse.class);

        if (response == null) {
            throw new RuntimeException("Failed to fetch account data for userId: " + userId);
        }
        return response;
    }
}
