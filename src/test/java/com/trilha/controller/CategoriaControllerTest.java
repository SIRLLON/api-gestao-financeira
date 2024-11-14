package com.trilha.controller;

import com.trilha.model.Categoria;
import com.trilha.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoriaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private CategoriaController categoriaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(categoriaController).build();
    }

    @Test
    void createCategoria_ShouldReturnCreatedCategoria() throws Exception {
        Categoria categoria = new Categoria(1L, "Categoria 1");
        when(categoriaService.saveCategoria(any(Categoria.class))).thenReturn(categoria);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Categoria 1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Categoria 1"));
    }

    @Test
    void getAllCategorias_ShouldReturnListOfCategorias() throws Exception {
        List<Categoria> categorias = new ArrayList<>();
        categorias.add(new Categoria(1L, "Categoria 1"));
        categorias.add(new Categoria(2L, "Categoria 2"));

        when(categoriaService.getAllCategorias()).thenReturn(categorias);

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getCategoriaById_ShouldReturnCategoria() throws Exception {
        Categoria categoria = new Categoria(1L, "Categoria 1");
        when(categoriaService.getCategoriaById(1L)).thenReturn(Optional.of(categoria));

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Categoria 1"));
    }

    @Test
    void getCategoriaById_ShouldReturnNotFound() throws Exception {
        when(categoriaService.getCategoriaById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCategoria_ShouldReturnUpdatedCategoria() throws Exception {
        Categoria categoria = new Categoria(1L, "Categoria 1");
        when(categoriaService.updateCategoria(anyLong(), any(Categoria.class))).thenReturn(Optional.of(categoria));

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Categoria 1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Categoria 1"));
    }

    @Test
    void updateCategoria_ShouldReturnNotFound() throws Exception {
        when(categoriaService.updateCategoria(anyLong(), any(Categoria.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\": \"Categoria 1\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategoria_ShouldReturnNoContent() throws Exception {
        doNothing().when(categoriaService).deleteCategoria(1L);

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isNoContent());
    }
}
