package com.trilha.controller;

import com.trilha.model.Categoria;
import com.trilha.model.CurrencyConversionRequest;
import com.trilha.model.CurrencyConversionResult;
import com.trilha.model.Transacao;
import com.trilha.service.ExchangeRateService;
import com.trilha.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CurrencyConversionResult> convertCurrency(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam Double valor) {
        CurrencyConversionResult convertedValue = exchangeRateService.convertCurrency(from, to, valor);
        return ResponseEntity.ok(convertedValue);
    }

    @PostMapping("/converter")
    public ResponseEntity<CurrencyConversionResult> convertCurrency(@RequestBody CurrencyConversionRequest request) {
        CurrencyConversionResult convertedValue = exchangeRateService.convertCurrency(request.getFrom(), request.getTo(), request.getValor());
        return ResponseEntity.ok(convertedValue);
    }

    @PostMapping
    public ResponseEntity<Transacao> createOrUpdateTransaction(@RequestBody Transacao transacao) {
        Transacao savedTransacao = transacaoService.saveOrUpdateTransaction(transacao);
        return ResponseEntity.ok(savedTransacao);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transacao> getTransactionById(@PathVariable Long id) {
        Optional<Transacao> transaction = transacaoService.getTransactionById(id);
        return transaction.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Buscar todas as transações
    @GetMapping
    public ResponseEntity<List<Transacao>> getAllTransactions() {
        List<Transacao> transacoes = transacaoService.getAllTransactions();
        return ResponseEntity.ok(transacoes);
    }

    // Excluir uma transação por ID
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
