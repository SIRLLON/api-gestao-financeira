package com.trilha.repository;

import com.trilha.model.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoria;

    @BeforeEach
    public void setUp() {
        // Cria uma nova categoria para o teste
        categoria = new Categoria();
        categoria.setName("Categoria Teste");
    }

    @Test
    public void testSaveAndFindById() {
        // Salva a categoria
        Categoria savedCategoria = categoriaRepository.save(categoria);

        // Recupera a categoria pelo ID
        Categoria foundCategoria = categoriaRepository.findById(savedCategoria.getId()).orElse(null);

        // Verifica se a categoria foi salva e recuperada corretamente
        assertEquals(savedCategoria.getId(), foundCategoria.getId());
        assertEquals(savedCategoria.getName(), foundCategoria.getName());
    }
}
