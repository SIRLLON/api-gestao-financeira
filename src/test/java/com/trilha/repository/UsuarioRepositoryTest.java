package com.trilha.repository;

import com.trilha.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        // Cria um novo usuário para o teste
        usuario = new Usuario("Usuario Teste", "teste@exemplo.com", "senha123");
    }

    @Test
    public void testSaveAndFindById() {
        // Salva o usuário
        Usuario savedUsuario = usuarioRepository.save(usuario);

        // Recupera o usuário pelo ID
        Usuario foundUsuario = usuarioRepository.findById(savedUsuario.getId()).orElse(null);

        // Verifica se o usuário foi salvo e recuperado corretamente
        assertEquals(savedUsuario.getId(), foundUsuario.getId());
        assertEquals(savedUsuario.getNome(), foundUsuario.getNome());
        assertEquals(savedUsuario.getEmail(), foundUsuario.getEmail());
    }
}
