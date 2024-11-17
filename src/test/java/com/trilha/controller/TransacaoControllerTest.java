package com.trilha.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trilha.config.JwtUtil;
import com.trilha.dto.TransactionRequest;
import com.trilha.dto.TransactionResponse;
import com.trilha.dto.UsuarioResponse;
import com.trilha.model.Categoria;
import com.trilha.model.Transacao;
import com.trilha.service.TransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransacaoController.class)
public class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransacaoService transacaoService;

    @MockBean
    private JwtUtil jwtUtil; // Adicione isso

    private Transacao transacao;

    @BeforeEach
    public void setUp() {
        transacao = new Transacao();
        transacao.setId(1L);
        // Configure outros atributos da transacao conforme necessário
    }

    @Test
    @WithMockUser(roles = {"TESTSC"}) // Simula um usuário com a role TESTSC
    public void testCreateTransacao() throws Exception {
        // Criação de um objeto TransactionRequest para enviar no corpo da requisição
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setDescricao("Compra de pão");
        transactionRequest.setValor(20.0);
        transactionRequest.setData(LocalDate.parse("2024-09-19"));
        transactionRequest.setUsuarioId(1L);  // Supondo que o ID do usuário seja 1
        transactionRequest.setCategoriaId(1L);  // Supondo que o ID da categoria seja 1

        // Criar o objeto TransactionResponse esperado
        TransactionResponse transactionResponse = new TransactionResponse(
                1L,  // ID da transação
                "Compra de pão",  // Descrição
                20.0,  // Valor
                "2024-09-19",  // Data formatada como String
                22.5,  // Valor convertido (exemplo fictício)
                1.125,  // Taxa de câmbio (exemplo fictício)
                new UsuarioResponse(1L, "Rafael", "rafaeln@email.com"),  // Exemplo de usuário retornado
                new Categoria(1L, "Alimentação")  // Exemplo de categoria retornada
        );

        // Mock do serviço para retornar o TransactionResponse criado
        when(transacaoService.createTransacao(any(TransactionRequest.class))).thenReturn(transactionResponse);

        // Execução do teste
        mockMvc.perform(post("/api/transacao")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))  // Enviando o TransactionRequest
                .andExpect(status().isOk())  // Espera-se status 200
                .andExpect(jsonPath("$.id").value(transactionResponse.getId()))  // Verifica se o ID da transação retornada é igual
                .andExpect(jsonPath("$.descricao").value(transactionResponse.getDescricao()))  // Verifica se a descrição está correta
                .andExpect(jsonPath("$.valor").value(transactionResponse.getValorOriginal()))  // Verifica se o valor está correto
                .andExpect(jsonPath("$.data").value(transactionResponse.getData()))  // Verifica se a data está correta
                .andExpect(jsonPath("$.usuario.id").value(transactionResponse.getUsuario().getId()))  // Verifica se o ID do usuário está correto
                .andExpect(jsonPath("$.usuario.nome").value(transactionResponse.getUsuario().getNome()))  // Verifica o nome do usuário
                .andExpect(jsonPath("$.usuario.email").value(transactionResponse.getUsuario().getEmail()))  // Verifica o e-mail do usuário
                .andExpect(jsonPath("$.categoria.id").value(transactionResponse.getCategoria().getId()))  // Verifica o ID da categoria
                .andExpect(jsonPath("$.categoria.name").value(transactionResponse.getCategoria().getName()));  // Verifica o nome da categoria
    }

    @Test
    @WithMockUser(roles = {"TESTSC"}) // Simula um usuário com a role TESTSC
    public void testGetTransactionByIdFound() throws Exception {
        Long id = 1L;
        when(transacaoService.getTransactionById(id)).thenReturn(Optional.of(transacao));

        mockMvc.perform(get("/api/transacao/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transacao.getId())); // Ajuste conforme o seu objeto Transacao
    }

    @Test
    @WithMockUser(roles = {"TESTSC"}) // Simula um usuário com a role TESTSC
    public void testGetTransactionByIdNotFound() throws Exception {
        Long id = 1L;
        when(transacaoService.getTransactionById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/transacao/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"TESTSC"}) // Simula um usuário com a role TESTSC
    public void testGetAllTransactions() throws Exception {
        List<Transacao> transacoes = new ArrayList<>();
        transacoes.add(transacao);
        when(transacaoService.getAllTransactions()).thenReturn(transacoes);

        mockMvc.perform(get("/api/transacao"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(transacoes.size())));
    }

    @Test
    @WithMockUser(roles = {"TESTSC"}) // Simula um usuário com a role TESTSC
    public void testDeleteTransactionById() throws Exception {
        Long id = 1L;
        doNothing().when(transacaoService).deleteTransactionById(id);

        mockMvc.perform(delete("/api/transacao/{id}", id)
                .with(csrf())) // Inclui o CSRF token na requisição
.andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"TESTSC"}) // Simula um usuário com a role TESTSC
    public void testGetExpenseSummary() throws Exception {
        Long userId = 1L;
        Map<Categoria, Double> summary = new HashMap<>();
        summary.put(new Categoria(), 100.0); // Configure conforme necessário
        when(transacaoService.getExpenseSummaryByCategory(userId)).thenReturn(summary);

        mockMvc.perform(get("/api/transacao/summary")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue())); // Ajuste conforme necessário
    }
}
