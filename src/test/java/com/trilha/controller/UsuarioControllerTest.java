package com.trilha.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trilha.config.JwtUtil;
import com.trilha.dto.UsuarioRequest;
import com.trilha.model.Usuario;
import com.trilha.service.ExcelImportService;
import com.trilha.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UsuarioController.class)
@ActiveProfiles("test")class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private ExcelImportService excelImportService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setEmail("usuario@example.com");
        usuario.setSenha("senha123");
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"TESTSC"})
    void whenCreateUser_thenReturnCreatedUser() throws Exception {
        // Cria um objeto UsuarioRequest com dados de exemplo
        UsuarioRequest usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNome("Usuário Teste");
        usuarioRequest.setEmail("usuario@example.com");
        usuarioRequest.setSenha("senha123");

        // Mock do serviço para retornar uma resposta fictícia
        Map<String, Object> response = Map.of(
                "id", 1L,
                "nome", usuarioRequest.getNome(),
                "email", usuarioRequest.getEmail()
        );
        when(usuarioService.createUserWithAccount(any(UsuarioRequest.class))).thenReturn(response);

        // Realiza a requisição POST para criar o usuário
        mockMvc.perform(post("/api/usuarios")
                        .with(csrf()) // Inclui o CSRF token na requisição
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andExpect(status().isCreated()) // Espera o status HTTP 201 (Created)
                .andExpect(jsonPath("$.id").value(response.get("id")))
                .andExpect(jsonPath("$.nome").value(response.get("nome")))
                .andExpect(jsonPath("$.email").value(response.get("email")));

        // Verifica que o método do serviço foi chamado
        verify(usuarioService, times(1)).createUserWithAccount(any(UsuarioRequest.class));
    }


    @Test
    @WithMockUser(username = "testUser", roles = {"TESTSC"})
    void whenGetAllUsers_thenReturnUserList() throws Exception {
        List<Usuario> users = Arrays.asList(usuario);
        when(usuarioService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(users.size()))
                .andExpect(jsonPath("$[0].id").value(usuario.getId()))
                .andExpect(jsonPath("$[0].nome").value(usuario.getNome()))
                .andExpect(jsonPath("$[0].email").value(usuario.getEmail()));

        verify(usuarioService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"TESTSC"})
    void whenGetUserById_thenReturnUser() throws Exception {
        when(usuarioService.getUserById(usuario.getId())).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuarios/{id}", usuario.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$.nome").value(usuario.getNome()))
                .andExpect(jsonPath("$.email").value(usuario.getEmail()));

        verify(usuarioService, times(1)).getUserById(usuario.getId());
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"TESTSC"})
    void whenUpdateUser_thenReturnUpdatedUser() throws Exception {
        Usuario updatedUser = new Usuario();
        updatedUser.setId(usuario.getId());
        updatedUser.setNome("Usuário Atualizado");
        updatedUser.setEmail("usuarioatualizado@example.com");
        updatedUser.setSenha("novaSenha123");

        when(usuarioService.updateUser(eq(usuario.getId()), any(Usuario.class))).thenReturn(Optional.of(updatedUser));

        mockMvc.perform(put("/api/usuarios/{id}", usuario.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedUser.getId()))
                .andExpect(jsonPath("$.nome").value(updatedUser.getNome()))
                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()));

        verify(usuarioService, times(1)).updateUser(eq(usuario.getId()), any(Usuario.class));
    }

    @Test
    @WithMockUser(roles = {"TESTSC"}) // Simula um usuário com a role TESTSC
    void whenDeleteUser_thenReturnNoContent() throws Exception {
        doNothing().when(usuarioService).deleteUser(usuario.getId());

        mockMvc.perform(delete("/api/usuarios/{id}", usuario.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).deleteUser(usuario.getId());
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"TESTSC"})
    void whenImportUsersFromExcel_thenReturnSuccessMessage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "usuarios.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "some-data".getBytes());

        when(usuarioService.importUsersFromExcel(any(MultipartFile.class), any(ExcelImportService.class)))
                .thenReturn("Importação realizada com sucesso.");

        mockMvc.perform(multipart("/api/usuarios/import")
                        .file(file)
                        .with(csrf())) // Adiciona o token CSRF aqui
                .andExpect(status().isOk())
                .andExpect(content().string("Importação realizada com sucesso."));

        verify(usuarioService, times(1)).importUsersFromExcel(any(MultipartFile.class), any(ExcelImportService.class));
    }
}
