package com.trilha.service;

import com.trilha.model.Categoria;
import com.trilha.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Teste Categoria");
    }

    @Test
    void whenSaveCategoria_thenReturnSavedCategoria() {
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        Categoria savedCategoria = categoriaService.saveCategoria(categoria);
        assertThat(savedCategoria).isEqualTo(categoria);
        verify(categoriaRepository, times(1)).save(categoria);
    }

    @Test
    void whenGetAllCategorias_thenReturnCategoriaList() {
        when(categoriaRepository.findAll()).thenReturn(Collections.singletonList(categoria));
        List<Categoria> categorias = categoriaService.getAllCategorias();
        assertThat(categorias).hasSize(1).contains(categoria);
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void whenGetCategoriaById_thenReturnCategoria() {
        when(categoriaRepository.findById(categoria.getId())).thenReturn(Optional.of(categoria));
        Optional<Categoria> foundCategoria = categoriaService.getCategoriaById(categoria.getId());
        assertThat(foundCategoria).isPresent().contains(categoria);
        verify(categoriaRepository, times(1)).findById(categoria.getId());
    }

    @Test
    void whenUpdateCategoria_thenReturnUpdatedCategoria() {
        when(categoriaRepository.existsById(categoria.getId())).thenReturn(true);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        categoria.setNome("Nome Atualizado");
        Optional<Categoria> updatedCategoria = categoriaService.updateCategoria(categoria.getId(), categoria);
        assertThat(updatedCategoria).isPresent().contains(categoria);
        verify(categoriaRepository, times(1)).save(categoria);
    }

    @Test
    void whenUpdateNonExistentCategoria_thenReturnEmpty() {
        when(categoriaRepository.existsById(categoria.getId())).thenReturn(false);
        Optional<Categoria> updatedCategoria = categoriaService.updateCategoria(categoria.getId(), categoria);
        assertThat(updatedCategoria).isNotPresent();
        verify(categoriaRepository, times(0)).save(categoria);
    }

    @Test
    void whenDeleteCategoria_thenCategoriaIsDeleted() {
        when(categoriaRepository.existsById(categoria.getId())).thenReturn(true);
        doNothing().when(categoriaRepository).deleteById(categoria.getId());
        categoriaService.deleteCategoria(categoria.getId());
        verify(categoriaRepository, times(1)).deleteById(categoria.getId());
    }

    @Test
    void whenDeleteNonExistentCategoria_thenDoNothing() {
        when(categoriaRepository.existsById(categoria.getId())).thenReturn(false);
        categoriaService.deleteCategoria(categoria.getId());
        verify(categoriaRepository, times(0)).deleteById(categoria.getId());
    }
}
