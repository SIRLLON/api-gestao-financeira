package com.trilha.controller;

import com.trilha.dto.UsuarioRequest;
import com.trilha.model.Usuario;
import com.trilha.service.ExcelImportService;
import com.trilha.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody UsuarioRequest usuarioRequest) {
        Map<String, Object> response = usuarioService.createUserWithAccount(usuarioRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsers() {
        List<Usuario> users = usuarioService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUserById(@PathVariable Long id) {
        Optional<Usuario> user = usuarioService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUser(@PathVariable Long id, @RequestBody Usuario usuario) {
        Optional<Usuario> updatedUser = usuarioService.updateUser(id, usuario);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TESTSC')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        usuarioService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Autowired
    private ExcelImportService excelImportService;

    // Endpoint para importar usuários via planilha Excel
    @PostMapping("/import")
    @PreAuthorize("hasRole('TESTSC')")
    public ResponseEntity<String> importUsersFromExcel(@RequestParam("file") MultipartFile file) {
        String result = usuarioService.importUsersFromExcel(file, excelImportService);
        return ResponseEntity.ok(result);
    }
}
