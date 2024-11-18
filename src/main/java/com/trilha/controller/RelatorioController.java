package com.trilha.controller;

import com.trilha.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Controlador responsável por operações relacionadas aos relatórios.
 */
@RestController
@RequestMapping("/api/relatorios")
@Tag(name = "Relatórios", description = "Endpoints para geração de relatórios em formato Excel.")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    /**
     * Exporta um relatório de transações em formato Excel.
     *
     * @return Arquivo Excel contendo o relatório das transações.
     * @throws IOException Caso ocorra algum erro durante a geração do relatório.
     */
    @Operation(summary = "Exportar relatório de transações",
            description = "Gera e retorna um arquivo Excel contendo os dados de todas as transações registradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao gerar o relatório.")
    })
    @GetMapping("/transacoes")
    public ResponseEntity<byte[]> exportarRelatorioDeTransacoes() throws IOException {
        byte[] relatorioExcel = relatorioService.gerarRelatorioDeTransacoes();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=relatorio-transacoes.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(relatorioExcel);
    }
}
