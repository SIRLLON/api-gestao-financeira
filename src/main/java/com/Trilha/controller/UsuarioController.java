package com.Trilha.controller;

import com.Trilha.model.Usuario;
import com.Trilha.service.ExcelImportService;
import com.Trilha.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Criar um novo usuário
    @PostMapping
    public ResponseEntity<Usuario> createUser(@RequestBody Usuario usuario) {
        Usuario createdUsuario = usuarioService.saveUser(usuario);
        return ResponseEntity.ok(createdUsuario);
    }

    // Obter todos os usuários
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsers() {
        List<Usuario> users = usuarioService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Obter um usuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUserById(@PathVariable Long id) {
        Optional<Usuario> user = usuarioService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Atualizar um usuário
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUser(@PathVariable Long id, @RequestBody Usuario usuario) {
        Optional<Usuario> updatedUser = usuarioService.updateUser(id, usuario);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Deletar um usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (usuarioService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Autowired
    private ExcelImportService excelImportService;

    // Endpoint para importar usuários via planilha Excel
    @PostMapping("/import")
    public ResponseEntity<String> importUsersFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            excelImportService.importUsersFromExcel(file);
            return new ResponseEntity<>("Users imported successfully!", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Error occurred while processing the file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
