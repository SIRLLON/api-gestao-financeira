package com.trilha.service;

import com.trilha.model.Categoria;
import com.trilha.model.Transacao;
import com.trilha.repository.CategoriaRepository;
import com.trilha.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;

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

    public List<Transacao> getTransactionByUserId(Long usuarioId) {
        return transacaoRepository.findByUsuarioId(usuarioId);
    }

    public Map<Categoria, Double> getExpenseSummaryByCategory(Long usuarioId) {
        List<Transacao> transacoes = getTransactionByUserId(usuarioId);
        return transacoes.stream()
                .collect(Collectors.groupingBy(
                        Transacao::getCategoria,
                        Collectors.summingDouble(Transacao::getValor)
                ));
    }
}
