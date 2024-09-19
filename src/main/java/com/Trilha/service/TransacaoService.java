package com.Trilha.service;

import com.Trilha.model.Transacao;
import com.Trilha.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    public Transacao saveOrUpdateTransaction(Transacao transacao) {
        return transacaoRepository.save(transacao);
    }

    public Optional<Transacao> getTransactionById(Long id) {
        return transacaoRepository.findById(id);
    }

    public List<Transacao> getAllTransactions() {
        return transacaoRepository.findAll();
    }

    public void deleteTransactionById(Long id) {
        transacaoRepository.deleteById(id);
    }
}
