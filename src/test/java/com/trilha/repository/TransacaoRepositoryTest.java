package com.trilha.repository;

import com.trilha.model.Categoria;
import com.trilha.model.Transacao;
import com.trilha.model.Usuario;
import com.trilha.repository.TransacaoRepository;
import com.trilha.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
public class TransacaoRepositoryTest {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;
    private Categoria categoria;
    @Autowired
    private CategoriaRepository categoriaRepository;

    @BeforeEach
    public void setUp() {
        // Cria e salva um usuário para associar à transação
        usuario = new Usuario();
        usuario.setNome("Test User");
        usuario.setEmail("testuser@example.com");
        usuario.setSenha("senha"); // Defina a senha conforme necessário
        usuario = usuarioRepository.save(usuario);

        // Cria e salva uma categoria para a transação
        categoria = new Categoria();
        categoria.setNome("Categoria Teste");
        categoria = categoriaRepository.save(categoria); // Use o repositório adequado para salvar a categoria
    }

    @Test
    public void testFindByUsuarioId() {
        Transacao transacao1 = new Transacao("Compra", 50.0, LocalDate.now(), categoria);
        transacao1.setUsuario(usuario);
        transacaoRepository.save(transacao1);

        List<Transacao> transacoes = transacaoRepository.findByUsuarioId(usuario.getId());
        assertEquals(1, transacoes.size());
        assertEquals("Compra", transacoes.get(0).getDescricao());
    }
}
