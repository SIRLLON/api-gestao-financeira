package com.trilha.service;

import com.trilha.exception.UsuarioNotFoundException;
import com.trilha.model.Usuario;
import com.trilha.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario saveUser(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> getAllUsers() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getUserById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> updateUser(Long id, Usuario usuario) {
        if (usuarioRepository.existsById(id)) {
            usuario.setId(id);
            return Optional.of(usuarioRepository.save(usuario));
        }
        return Optional.empty();
    }

    public void deleteUser(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNotFoundException("Usuário com ID " + id + " não encontrado.");
        }
        usuarioRepository.deleteById(id);
    }

    public String importUsersFromExcel(MultipartFile file, ExcelImportService excelImportService) {
        try {
            excelImportService.importUsersFromExcel(file);
            return "Users imported successfully!";
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while processing the file.", e);
        }
    }
}
