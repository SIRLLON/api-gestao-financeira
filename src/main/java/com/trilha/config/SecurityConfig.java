package com.trilha.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    @Profile("!test") // Aplica essa configuração apenas se o perfil não for "test"
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF (não recomendado para produção sem tokens adequados)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Não manter sessão
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/token").permitAll() // Permitir acesso sem autenticação
                        .requestMatchers("/home").permitAll() // Permitir acesso ao endpoint /home
                        .requestMatchers("/h2-console/**").permitAll() // Permitir acesso ao H2 Console
                        .anyRequest().authenticated() // Todos os outros endpoints precisam de autenticação
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Adiciona o filtro JWT
                .headers(headers -> headers
                        .contentSecurityPolicy("frame-ancestors 'self'") // Permite o carregamento de iframe de mesma origem
                );

        return http.build();
    }
    // Configuração de segurança para ambiente de testes
    @Bean
    @Profile("test") // Aplica essa configuração apenas quando o perfil for "test"
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/token").permitAll() // Permitir acesso sem autenticação
                        .requestMatchers("/h2-console/**").permitAll() // Permitir acesso ao H2 Console
                        .requestMatchers("/home").permitAll() // Permitir acesso ao endpoint /home
                        .anyRequest().permitAll() // Libera todas as outras rotas para os testes
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Não manter sessão

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
