package com.trilha.controller;
import com.trilha.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/transacoes")
    public ResponseEntity<ByteArrayResource> gerarRelatorioTransacoes() throws IOException {
        byte[] relatorio = reportService.gerarRelatorioTransacoes();

        ByteArrayResource resource = new ByteArrayResource(relatorio);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_transacoes.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
