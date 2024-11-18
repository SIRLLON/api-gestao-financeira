package com.trilha.controller;

import com.trilha.dto.UsuarioRequest;
import com.trilha.model.Usuario;
import com.trilha.service.ExcelImportService;
import com.trilha.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    /**
     * Cria um novo usuário no sistema.
     *
     * @param usuarioRequest informações do usuário a ser criado
     * @return informações do usuário criado
     */
    @Operation(summary = "Criar um novo usuário", description = "Cria um novo usuário no sistema com base nos dados fornecidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(
            @RequestBody
            @Parameter(description = "Dados do usuário a ser criado", required = true)
            @Valid UsuarioRequest usuarioRequest) {
        Map<String, Object> response = usuarioService.createUserWithAccount(usuarioRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retorna a lista de todos os usuários cadastrados.
     *
     * @return lista de usuários
     */
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista de todos os usuários cadastrados no sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsers() {
        List<Usuario> users = usuarioService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Retorna um usuário pelo ID.
     *
     * @param id ID do usuário
     * @return informações do usuário
     */
    @Operation(summary = "Buscar usuário por ID", description = "Retorna as informações de um usuário com base no ID fornecido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUserById(
            @PathVariable
            @Parameter(description = "ID do usuário a ser buscado", example = "1") Long id) {
        Optional<Usuario> user = usuarioService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Atualiza as informações de um usuário.
     *
     * @param id ID do usuário a ser atualizado
     * @param usuario dados do usuário atualizados
     * @return informações do usuário atualizado
     */
    @Operation(summary = "Atualizar usuário", description = "Atualiza as informações de um usuário com base no ID fornecido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUser(
            @PathVariable
            @Parameter(description = "ID do usuário a ser atualizado", example = "1") Long id,
            @RequestBody
            @Parameter(description = "Dados atualizados do usuário") Usuario usuario) {
        Optional<Usuario> updatedUser = usuarioService.updateUser(id, usuario);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Exclui um usuário pelo ID.
     *
     * @param id ID do usuário
     */
    @Operation(summary = "Excluir usuário", description = "Exclui um usuário do sistema com base no ID fornecido.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TESTSC')")
    public ResponseEntity<Void> deleteUser(
            @PathVariable
            @Parameter(description = "ID do usuário a ser excluído", example = "1") Long id) {
        usuarioService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Autowired
    private ExcelImportService excelImportService;

    /**
     * Importa usuários a partir de um arquivo Excel.
     *
     * @param file arquivo Excel contendo informações dos usuários
     * @return mensagem indicando o resultado da importação
     */
    @Operation(summary = "Importar usuários via Excel", description = "Importa uma lista de usuários a partir de um arquivo Excel.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuários importados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao processar o arquivo", content = @Content)
    })
    @PostMapping("/import")
    @PreAuthorize("hasRole('TESTSC')")
    public ResponseEntity<String> importUsersFromExcel(
            @RequestParam("file")
            @Parameter(description = "Arquivo Excel contendo os usuários", required = true) MultipartFile file) {
        String result = usuarioService.importUsersFromExcel(file, excelImportService);
        return ResponseEntity.ok(result);
    }
}
