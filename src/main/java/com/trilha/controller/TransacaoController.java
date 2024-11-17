package com.trilha.controller;

import com.trilha.dto.TransactionRequest;
import com.trilha.dto.TransactionResponse;
import com.trilha.model.Categoria;
import com.trilha.model.Transacao;
import com.trilha.service.CategoriaService;
import com.trilha.service.ExchangeRateService;
import com.trilha.service.TransacaoService;
import com.trilha.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/transacao")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private ExchangeRateService exchangeRateService;


    @PostMapping
    public ResponseEntity<TransactionResponse> createTransacao(@RequestBody TransactionRequest transactionRequest) {
        // A exceção é tratada no serviço ou no @ControllerAdvice
        TransactionResponse transactionResponse = transacaoService.createTransacao(transactionRequest);
        return ResponseEntity.ok(transactionResponse);
    }

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
    public ResponseEntity<TransactionResponse> convertTransaction(@RequestBody @Valid TransactionRequest request) {
        // O serviço fará toda a lógica e retorna um `TransactionResponse`
        TransactionResponse transactionResponse = transacaoService.convertTransaction(request);
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
