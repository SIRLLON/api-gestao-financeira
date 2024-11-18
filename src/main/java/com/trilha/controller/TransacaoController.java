package com.trilha.controller;

import com.trilha.dto.TransactionRequest;
import com.trilha.dto.TransactionResponse;
import com.trilha.model.Categoria;
import com.trilha.model.Transacao;
import com.trilha.service.ExchangeRateService;
import com.trilha.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/transacao")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private ExchangeRateService exchangeRateService;

    /**
     * Cria uma nova transação no sistema.
     *
     * @param transactionRequest objeto com os dados da nova transação
     * @return resposta com os dados da transação criada
     */
    @Operation(summary = "Criar transação", description = "Adiciona uma nova transação no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransacao(
            @RequestBody
            @Valid
            @Parameter(description = "Dados da transação a ser criada", required = true) TransactionRequest transactionRequest) {
        TransactionResponse transactionResponse = transacaoService.createTransacao(transactionRequest);
        return ResponseEntity.ok(transactionResponse);
    }

    /**
     * Converte um valor entre duas moedas.
     *
     * @param from moeda de origem
     * @param to moeda de destino
     * @param valor valor a ser convertido
     * @return valor convertido e taxa de câmbio
     */
    @Operation(summary = "Converter moeda", description = "Converte um valor entre duas moedas usando a taxa de câmbio atual.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conversão realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content)
    })
    @GetMapping("/converter")
    public ResponseEntity<Map<String, Object>> convertCurrency(
            @RequestParam
            @Parameter(description = "Moeda de origem", example = "USD") String from,
            @RequestParam
            @Parameter(description = "Moeda de destino", example = "BRL") String to,
            @RequestParam
            @Parameter(description = "Valor a ser convertido", example = "100.0") Double valor) {
        ExchangeRateService.ConversionResult conversionResult = exchangeRateService.convertCurrency(from, to, valor);

        Map<String, Object> response = new HashMap<>();
        response.put("convertedValue", conversionResult.getConvertedValue());
        response.put("exchangeRate", conversionResult.getExchangeRate());

        return ResponseEntity.ok(response);
    }

    /**
     * Converte e cria uma nova transação com base na moeda de destino.
     *
     * @param request dados da transação a ser criada
     * @return transação convertida
     */
    @Operation(summary = "Converter e criar transação", description = "Converte o valor da transação para outra moeda e a registra no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transação convertida e criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PostMapping("/converter")
    public ResponseEntity<TransactionResponse> convertTransaction(
            @RequestBody
            @Valid
            @Parameter(description = "Dados da transação para conversão e registro", required = true) TransactionRequest request) {
        TransactionResponse transactionResponse = transacaoService.convertTransaction(request);
        return ResponseEntity.ok(transactionResponse);
    }

    /**
     * Busca uma transação pelo ID.
     *
     * @param id ID da transação
     * @return dados da transação correspondente
     */
    @Operation(summary = "Buscar transação por ID", description = "Retorna os dados de uma transação com base no ID fornecido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transação encontrada"),
            @ApiResponse(responseCode = "404", description = "Transação não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Transacao> getTransactionById(
            @PathVariable
            @Parameter(description = "ID da transação a ser buscada", example = "1") Long id) {
        Optional<Transacao> transaction = transacaoService.getTransactionById(id);
        return transaction.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Lista todas as transações registradas.
     *
     * @return lista de transações
     */
    @Operation(summary = "Listar transações", description = "Retorna todas as transações cadastradas no sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de transações retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<Transacao>> getAllTransactions() {
        List<Transacao> transacoes = transacaoService.getAllTransactions();
        return ResponseEntity.ok(transacoes);
    }

    /**
     * Busca transações realizadas nos últimos 7 dias a partir de uma data.
     *
     * @param date data inicial para cálculo dos 7 dias
     * @return lista de transações nos últimos 7 dias
     */
    @Operation(summary = "Buscar transações dos últimos 7 dias", description = "Retorna as transações realizadas nos últimos 7 dias a partir de uma data.")
    @ApiResponse(responseCode = "200", description = "Transações retornadas com sucesso")
    @GetMapping("/last-7-days")
    public ResponseEntity<List<Transacao>> getTransactionsLast7Days(
            @RequestParam("date")
            @Parameter(description = "Data inicial para cálculo", example = "2024-11-01")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<Transacao> transacoes = transacaoService.getTransactionsLast7Days(date);
        return ResponseEntity.ok(transacoes);
    }

    /**
     * Exclui uma transação pelo ID.
     *
     * @param id ID da transação
     */
    @Operation(summary = "Excluir transação", description = "Remove uma transação com base no ID fornecido.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transação excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Transação não encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionById(
            @PathVariable
            @Parameter(description = "ID da transação a ser excluída", example = "1") Long id) {
        transacaoService.deleteTransactionById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Resumo das despesas por categoria de um usuário.
     *
     * @param userId ID do usuário
     * @return mapa com categorias e valores totais
     */
    @Operation(summary = "Resumo de despesas por categoria", description = "Retorna o resumo das despesas agrupadas por categoria para um usuário.")
    @ApiResponse(responseCode = "200", description = "Resumo de despesas retornado com sucesso")
    @GetMapping("/summary")
    public ResponseEntity<Map<Categoria, Double>> getExpenseSummary(
            @RequestParam
            @Parameter(description = "ID do usuário", example = "1") Long userId) {
        Map<Categoria, Double> summary = transacaoService.getExpenseSummaryByCategory(userId);
        return ResponseEntity.ok(summary);
    }
}
