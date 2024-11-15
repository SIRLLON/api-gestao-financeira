package com.trilha.controller;

import com.trilha.model.Categoria;
import com.trilha.model.Transacao;
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
