package com.trilha.service;

import com.trilha.dto.TransactionRequest;
import com.trilha.dto.TransactionResponse;
import com.trilha.dto.UsuarioResponse;
import com.trilha.exception.CategoriaNotFoundException;
import com.trilha.exception.UsuarioNotFoundException;
import com.trilha.mapper.UsuarioMapper;
import com.trilha.model.Categoria;
import com.trilha.model.Transacao;
import com.trilha.model.Usuario;
import com.trilha.repository.CategoriaRepository;
import com.trilha.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Autowired
    private TransacaoRepository transacaoRepository;

    public TransactionResponse  createTransacao(TransactionRequest transactionRequest) {
        // Buscar usuário e categoria pelo ID
        Usuario usuario = usuarioService.getUserById(transactionRequest.getUsuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado"));

        Categoria categoria = categoriaService.getCategoriaById(transactionRequest.getCategoriaId())
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria não encontrada"));

        // Criar a transação com os dados
        Transacao transacao = new Transacao();
        transacao.setDescricao(transactionRequest.getDescricao());
        transacao.setValor(transactionRequest.getValor());
        transacao.setData(transactionRequest.getData());
        transacao.setUsuario(usuario);
        transacao.setCategoria(categoria);

        Transacao savedTransacao = transacaoRepository.save(transacao);
        UsuarioResponse usuarioResponse = UsuarioMapper.toUsuarioResponse(savedTransacao.getUsuario());
        String formattedDate = savedTransacao.getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Double convertedValue = null; // Se não for uma conversão, pode deixar como null
        Double exchangeRate = null; // Se não for uma conversão, pode deixar como null

        return new TransactionResponse(
                savedTransacao.getId(),
                savedTransacao.getDescricao(),
                savedTransacao.getValor(),
                formattedDate, // Data formatada
                convertedValue, // Valor convertido (null para criar transação)
                exchangeRate,   // Taxa de câmbio (null para criar transação)
                usuarioResponse,
                savedTransacao.getCategoria()
        );
    }

    public TransactionResponse convertTransaction(TransactionRequest request) {
        // Busca o usuário e a categoria para validar as IDs
        Usuario usuario = usuarioService.getUserById(request.getUsuarioId())
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado"));

        Categoria categoria = categoriaService.getCategoriaById(request.getCategoriaId())
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria não encontrada"));

        // Realiza a conversão de moeda
        ExchangeRateService.ConversionResult conversionResult = exchangeRateService.convertCurrency(
                request.getFrom(),
                request.getTo(),
                request.getValor()
        );

        // Formata a data para o formato desejado
        String formattedDate = request.getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Mapeia o usuário para o DTO de resposta
        UsuarioResponse usuarioResponse = UsuarioMapper.toUsuarioResponse(usuario);

        // Cria o objeto `TransactionResponse` com os dados fornecidos
        return new TransactionResponse(
                null, // ID não será salvo aqui
                request.getDescricao(),
                request.getValor(),
                request.getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                conversionResult.getConvertedValue(),
                conversionResult.getExchangeRate(),
                UsuarioMapper.toUsuarioResponse(usuario),
                categoria
        );
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
