package com.trilha.controller;

import com.trilha.dto.TransactionRequest;
import com.trilha.dto.TransactionResponse;
import com.trilha.model.Categoria;
import com.trilha.model.Transacao;
import com.trilha.service.CategoriaService;
import com.trilha.service.ExchangeRateService;
import com.trilha.service.TransacaoService;
import com.trilha.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/transacao")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping("/converter")
    public ResponseEntity<Map<String, Object>> convertCurrency(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam Double valor) {
        ExchangeRateService.ConversionResult conversionResult = exchangeRateService.convertCurrency(from, to, valor);

        Map<String, Object> response = new HashMap<>();
        response.put("convertedValue", conversionResult.getConvertedValue());
        response.put("exchangeRate", conversionResult.getExchangeRate());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/converter")
    public ResponseEntity<Map<String, Object>> convertTransaction(@RequestBody TransactionRequest request) {
        ExchangeRateService.ConversionResult conversionResult = exchangeRateService.convertCurrency(request.getFrom(), request.getTo(), request.getValor());

        Map<String, Object> response = new HashMap<>();
        response.put("descricao", request.getDescricao());
        response.put("valorOriginal", request.getValor());
        response.put("convertedValue", conversionResult.getConvertedValue());
        response.put("exchangeRate", conversionResult.getExchangeRate());
        response.put("data", request.getData());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransacao(@RequestBody TransactionRequest transactionRequest) {
        // A exceção é tratada no serviço ou no @ControllerAdvice
        TransactionResponse transactionResponse = transacaoService.createTransacao(transactionRequest);
        return ResponseEntity.ok(transactionResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transacao> getTransactionById(@PathVariable Long id) {
        Optional<Transacao> transaction = transacaoService.getTransactionById(id);
        return transaction.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Transacao>> getAllTransactions() {
        List<Transacao> transacoes = transacaoService.getAllTransactions();
        return ResponseEntity.ok(transacoes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionById(@PathVariable Long id) {
        transacaoService.deleteTransactionById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<Categoria, Double>> getExpenseSummary(
            @RequestParam Long userId) {
        Map<Categoria, Double> summary = transacaoService.getExpenseSummaryByCategory(userId);
        return ResponseEntity.ok(summary);
    }
}
