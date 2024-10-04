package com.trilha.service;

import com.trilha.exception.UsuarioNotFoundException;
import com.trilha.model.Categoria;
import com.trilha.model.Transacao;
import com.trilha.model.Usuario;
import com.trilha.repository.TransacaoRepository;
import com.trilha.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TransacaoRepository transacaoRepository;

    public Transacao saveOrUpdateTransaction(Transacao transacao) {
        if (transacao.getUsuario() != null && transacao.getUsuario().getId() != null) {
            Usuario usuarioExistente = usuarioRepository.findById(transacao.getUsuario().getId())
                    .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado com ID: " + transacao.getUsuario().getId()));

            transacao.setUsuario(usuarioExistente);
        }

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
