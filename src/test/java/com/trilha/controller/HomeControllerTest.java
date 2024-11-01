package com.trilha.controller;

import com.trilha.config.JwtAuthenticationFilter;
import com.trilha.config.JwtUtil;
import com.trilha.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class) // Desative a segurança ou configure-a para permitir acesso
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private JwtUtil jwtUtil; // Mock do JwtUtil, caso seja necessário

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks
    }

    @Test
    void whenHome_thenReturnWelcomeMessage() throws Exception {
        mockMvc.perform(get("/home")
                        .with(user("user").password("password").roles("USER")) // Mock do usuário
                        .with(csrf()) // Adiciona o CSRF token, se necessário
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Bem-vindo à aplicação do Sirllon!"));
    }

    @Configuration
    static class TestConfig {
        @Bean
        public JwtUtil jwtUtil() {
            return new JwtUtil("secret"); // Ou outro valor para a chave secreta
        }
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil) {
            return new JwtAuthenticationFilter(jwtUtil);
        }
    }
}
