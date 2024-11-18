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
        usuario.setSaldo(1000.0);
        usuario = usuarioRepository.save(usuario);

        // Cria e salva uma categoria para a transação
        categoria = new Categoria();
        categoria.setName("Categoria Teste");
        categoria = categoriaRepository.save(categoria); // Use o repositório adequado para salvar a categoria
    }

    @Test
    public void testFindByUsuarioId() {
        // Criação e salvamento de uma transação
        Transacao transacao1 = new Transacao();
        transacao1.setDescricao("Compra");
        transacao1.setValor(50.0);
        transacao1.setData(LocalDate.now());
        transacao1.setCategoria(categoria);
        transacao1.setUsuario(usuario);
        transacaoRepository.save(transacao1);        transacao1.setUsuario(usuario);
        transacaoRepository.save(transacao1);

        // Recuperando as transações do banco de dados
        List<Transacao> transacoes = transacaoRepository.findByUsuarioId(usuario.getId());

        // Verificando se a transação foi salva corretamente
        assertEquals(1, transacoes.size(), "Deve retornar uma transação");
        assertEquals("Compra", transacoes.get(0).getDescricao(), "A descrição da transação deve ser 'Compra'");
        assertEquals(usuario.getId(), transacoes.get(0).getUsuario().getId(), "O ID do usuário deve ser o mesmo");
    }
}
