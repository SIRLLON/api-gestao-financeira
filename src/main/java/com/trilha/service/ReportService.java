package com.trilha.service;
import com.trilha.model.Transacao;
import com.trilha.repository.TransacaoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


@Service
public class ReportService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    public byte[] gerarRelatorioTransacoes() throws IOException {
        List<Transacao> transacoes = transacaoRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Relatório de Transações");

            // Cria o cabeçalho
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Usuário");
            headerRow.createCell(2).setCellValue("Valor");
            headerRow.createCell(3).setCellValue("Data");
            headerRow.createCell(4).setCellValue("Tipo");

            // Preenche os dados das transações
            int rowNum = 1;
            for (Transacao transacao : transacoes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(transacao.getId());
                row.createCell(1).setCellValue(transacao.getUsuario().getNome()); // ou outro identificador de usuário
                row.createCell(2).setCellValue(transacao.getValor());
                row.createCell(3).setCellValue(transacao.getData().toString());
                row.createCell(4).setCellValue(transacao.getCategoria().toString());
            }

            // Gera o relatório em bytes
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                workbook.write(out);
                return out.toByteArray();
            }
        }
    }
}
