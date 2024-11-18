package com.trilha.service;

import com.trilha.model.Transacao;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class RelatorioService {

    @Autowired
    private TransacaoService transacaoService;

    public byte[] gerarRelatorioDeTransacoes() throws IOException {
        List<Transacao> transacoes = transacaoService.getAllTransactions();

        // Formatação de dados
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Criação do arquivo Excel
        Workbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = (XSSFSheet) workbook.createSheet("Relatório de Transações");

        // Estilos de células
        DataFormat dataFormat = workbook.createDataFormat();

        // Estilo de moeda para Valor (moeda de destino)
        CellStyle currencyStyle = workbook.createCellStyle();
        // Estilo de moeda para Saldo (sempre BRL)
        CellStyle brlCurrencyStyle = workbook.createCellStyle();
        brlCurrencyStyle.setDataFormat(dataFormat.getFormat("R$#,##0.00")); // Formato BRL

        // Estilo numérico
        CellStyle numericStyle = workbook.createCellStyle();
        numericStyle.setDataFormat(dataFormat.getFormat("0.0000")); // Estilo numérico com 4 casas decimais

        // Criação do cabeçalho
        Row headerRow = sheet.createRow(0);
        String[] colunas = {
                "ID", "Usuário", "Descrição", "Valor (Moeda de Origem)", "Origem", "Taxa de Câmbio",
                "Destino", "Valor (Moeda de Destino)", "Data", "Categoria", "Saldo (R$)"
        };
        for (int i = 0; i < colunas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(colunas[i]);
        }

        // Preenchendo os dados
        int rowNum = 1;
        for (Transacao transacao : transacoes) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(transacao.getId()); // ID

            // Nome do usuário
            row.createCell(1).setCellValue(
                    transacao.getUsuario() != null ? transacao.getUsuario().getNome() : "Não informado"
            );

            row.createCell(2).setCellValue(transacao.getDescricao()); // Descrição

            // Valor (Moeda de Origem)
            Cell valorOrigemCell = row.createCell(3);
            if (transacao.getValor() != null && transacao.getOrigem() != null) {
                String moedaOrigem = transacao.getOrigem(); // Moeda de Origem (Ex: BRL, USD, etc.)
                Locale locale = new Locale("pt", "BR");

                // Caso a origem seja uma moeda diferente de BRL, vamos formatar de acordo com a moeda
                if ("EUR".equals(moedaOrigem)) {
                    locale = new Locale("de", "DE"); // Para Euro (EUR)
                } else if ("USD".equals(moedaOrigem)) {
                    locale = Locale.US; // Para Dólar (USD)
                }

                // Formatando de acordo com a moeda de origem
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
                valorOrigemCell.setCellValue(transacao.getValor());
                valorOrigemCell.setCellStyle(currencyStyle);
                currencyStyle.setDataFormat(dataFormat.getFormat(numberFormat.format(transacao.getValor())));
            } else {
                valorOrigemCell.setCellValue("N/A");
            }

            // Origem
            Cell origemCell = row.createCell(4);
            origemCell.setCellValue(transacao.getOrigem() != null ? transacao.getOrigem() : "Não informado");

            // Taxa de câmbio formatada
            Cell exchangeRateCell = row.createCell(5);
            if (transacao.getExchangeRate() != null) {
                exchangeRateCell.setCellValue(transacao.getExchangeRate());
                exchangeRateCell.setCellStyle(numericStyle);
            } else {
                exchangeRateCell.setCellValue("N/A");
            }

            // Destino
            Cell destinoCell = row.createCell(6);
            destinoCell.setCellValue(transacao.getDestino() != null ? transacao.getDestino() : "Não informado");

            // Valor (Moeda de Destino)
            Cell valorDestinoCell = row.createCell(7);
            if (transacao.getConvertedValue() != null && transacao.getDestino() != null) {
                String moedaDestino = transacao.getDestino(); // Moeda de Destino (Ex: EUR, USD, etc.)
                Locale locale = new Locale("pt", "BR");

                // Caso o destino seja uma moeda diferente de BRL, vamos formatar de acordo com a moeda
                if ("EUR".equals(moedaDestino)) {
                    locale = new Locale("de", "DE"); // Para Euro (EUR)
                } else if ("USD".equals(moedaDestino)) {
                    locale = Locale.US; // Para Dólar (USD)
                }

                // Formatando de acordo com a moeda de destino
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
                valorDestinoCell.setCellValue(transacao.getConvertedValue());
                // Criando um estilo de célula diretamente com o formato correto para moeda
                CellStyle destinationCurrencyStyle = workbook.createCellStyle();
                destinationCurrencyStyle.setDataFormat(dataFormat.getFormat(numberFormat.format(transacao.getConvertedValue())));

                // Aplicando o estilo de moeda correto de acordo com a moeda de destino
                valorDestinoCell.setCellStyle(destinationCurrencyStyle);
            } else {
                valorDestinoCell.setCellValue("N/A");
            }

            // Data formatada
            Cell dataCell = row.createCell(8);
            dataCell.setCellValue(
                    transacao.getData() != null ? transacao.getData().format(dateFormatter) : "N/A"
            );

            // Nome da categoria
            row.createCell(9).setCellValue(
                    transacao.getCategoria() != null ? transacao.getCategoria().getName() : "Não informado"
            );

            // Saldo do usuário formatado como moeda
            Cell saldoCell = row.createCell(10);
            if (transacao.getUsuario() != null && transacao.getUsuario().getSaldo() != null) {
                saldoCell.setCellValue(transacao.getUsuario().getSaldo());
                saldoCell.setCellStyle(brlCurrencyStyle);  // Aplica o formato de moeda BRL
            } else {
                saldoCell.setCellValue("N/A");
            }
        }

        // Ajustando automaticamente o tamanho das colunas
        for (int i = 0; i < colunas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Convertendo para array de bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

}


