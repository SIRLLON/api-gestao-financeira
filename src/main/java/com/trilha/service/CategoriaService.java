package com.trilha.service;

import com.trilha.model.Categoria;
import com.trilha.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Salvar ou criar uma nova categoria
    public Categoria saveCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    // Obter todas as categorias
    public List<Categoria> getAllCategorias() {
        return categoriaRepository.findAll();
    }

    // Obter uma categoria por ID
    public Optional<Categoria> getCategoriaById(Long id) {
        return categoriaRepository.findById(id);
    }

    // Atualizar uma categoria existente
    public Optional<Categoria> updateCategoria(Long id, Categoria categoria) {
        if (categoriaRepository.existsById(id)) {
            categoria.setId(id);
            return Optional.of(categoriaRepository.save(categoria));
        }
        return Optional.empty();
    }

    // Deletar uma categoria por ID
    public void deleteCategoria(Long id) {
        if (categoriaRepository.existsById(id)) {
            categoriaRepository.deleteById(id);
        }
    }
}
