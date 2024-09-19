package com.Trilha.service;

import com.Trilha.model.Usuario;
import com.Trilha.repository.UsuarioRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelImportService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Método para ler a planilha e salvar os dados no banco
    public void importUsersFromExcel(MultipartFile file) throws IOException {
        List<Usuario> usuarios = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0); // Seleciona a primeira linha

            // aqui começando da segunda linha
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);

                Usuario usuario = new Usuario();
                usuario.setNome(row.getCell(0).getStringCellValue());  // Nome na coluna A
                usuario.setEmail(row.getCell(1).getStringCellValue()); // Email na coluna B
                usuario.setSenha(row.getCell(2).getStringCellValue()); // Senha na coluna C

                usuarios.add(usuario);
            }
        }
        // Salva todos os usuários no banco de dados
        usuarioRepository.saveAll(usuarios);
    }
}
