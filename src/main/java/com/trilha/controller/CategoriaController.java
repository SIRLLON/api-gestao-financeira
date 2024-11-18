package com.trilha.controller;

import com.trilha.dto.CategoriaRequest;
import com.trilha.model.Categoria;
import com.trilha.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    /**
     * Cria uma nova categoria no sistema.
     *
     * @param categoriaRequest objeto contendo os dados da nova categoria
     * @return categoria criada
     */
    @Operation(summary = "Criar uma nova categoria", description = "Cria uma nova categoria no sistema com base nos dados fornecidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Categoria> createCategoria(
            @RequestBody
            @Parameter(description = "Dados da categoria a ser criada", required = true) CategoriaRequest categoriaRequest) {
        Categoria categoria = new Categoria();
        categoria.setName(categoriaRequest.getName());
        Categoria createdCategoria = categoriaService.saveCategoria(categoria);
        return ResponseEntity.ok(createdCategoria);
    }

    /**
     * Retorna todas as categorias cadastradas.
     *
     * @return lista de categorias
     */
    @Operation(summary = "Listar todas as categorias", description = "Retorna uma lista de todas as categorias cadastradas no sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<Categoria>> getAllCategorias() {
        List<Categoria> categorias = categoriaService.getAllCategorias();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Retorna uma categoria pelo ID.
     *
     * @param id ID da categoria
     * @return categoria correspondente ao ID fornecido
     */
    @Operation(summary = "Buscar categoria por ID", description = "Retorna as informações de uma categoria com base no ID fornecido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getCategoriaById(
            @PathVariable
            @Parameter(description = "ID da categoria a ser buscada", example = "1") Long id) {
        Optional<Categoria> categoria = categoriaService.getCategoriaById(id);
        return categoria.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Atualiza as informações de uma categoria.
     *
     * @param id ID da categoria a ser atualizada
     * @param categoria objeto contendo os novos dados da categoria
     * @return categoria atualizada
     */
    @Operation(summary = "Atualizar categoria", description = "Atualiza os dados de uma categoria existente com base no ID fornecido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> updateCategoria(
            @PathVariable
            @Parameter(description = "ID da categoria a ser atualizada", example = "1") Long id,
            @RequestBody
            @Parameter(description = "Dados atualizados da categoria", required = true) Categoria categoria) {
        Optional<Categoria> updatedCategoria = categoriaService.updateCategoria(id, categoria);
        return updatedCategoria.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Exclui uma categoria pelo ID.
     *
     * @param id ID da categoria a ser excluída
     */
    @Operation(summary = "Excluir categoria", description = "Exclui uma categoria com base no ID fornecido.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(
            @PathVariable
            @Parameter(description = "ID da categoria a ser excluída", example = "1") Long id) {
        categoriaService.deleteCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
