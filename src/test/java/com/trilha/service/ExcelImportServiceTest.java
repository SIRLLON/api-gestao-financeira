package com.trilha.service;

import com.trilha.model.Usuario;
import com.trilha.repository.UsuarioRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class ExcelImportServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ExcelImportService excelImportService;

    private MultipartFile file;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenImportUsersFromExcel_thenUsersAreSaved() throws Exception {
        // Criação de uma planilha em memória
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Usuarios");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Nome");
            headerRow.createCell(1).setCellValue("Email");
            headerRow.createCell(2).setCellValue("Senha");

            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("Usuário1");
            row1.createCell(1).setCellValue("email1@example.com");
            row1.createCell(2).setCellValue("senha1");

            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("Usuário2");
            row2.createCell(1).setCellValue("email2@example.com");
            row2.createCell(2).setCellValue("senha2");

            // Criar um InputStream para a planilha
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                byte[] bytes = outputStream.toByteArray();
                InputStream is = new ByteArrayInputStream(bytes);

                // Simulando o arquivo MultipartFile
                file = new MultipartFile() {
                    @Override
                    public String getName() {
                        return "usuarios.xlsx";
                    }

                    @Override
                    public String getOriginalFilename() {
                        return "usuarios.xlsx";
                    }

                    @Override
                    public String getContentType() {
                        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                    }

                    @Override
                    public boolean isEmpty() {
                        return false;
                    }

                    @Override
                    public long getSize() {
                        return bytes.length;
                    }

                    @Override
                    public byte[] getBytes() throws IOException {
                        return bytes;
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return is;
                    }

                    @Override
                    public void transferTo(File dest) throws IOException, IllegalStateException {
                    }
                };

                excelImportService.importUsersFromExcel(file);

                verify(usuarioRepository, times(1)).saveAll(anyList());
            }
        }
    }
}
