package com.Trilha.controller;

import com.Trilha.model.Transacao;
import com.Trilha.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transacao")
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

    // Buscar todas as transações
    @GetMapping
    public ResponseEntity<List<Transacao>> getAllTransactions() {
        List<Transacao> transacaos = transacaoService.getAllTransactions();
        return ResponseEntity.ok(transacaos);
    }

    // Excluir uma transação por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionById(@PathVariable Long id) {
        transacaoService.deleteTransactionById(id);
        return ResponseEntity.noContent().build();
    }
}
