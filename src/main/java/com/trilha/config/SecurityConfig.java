package com.trilha.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(auth -> auth
                        .requestMatchers( "/auth/token").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/transacao").hasRole("TESTSC") // Permitir POST para ROLE_TESTSC
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/import").hasRole("TESTSC") // Permitir POST para ROLE_TESTSC
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("TESTSC") // Permitir DELETE para ROLE_TESTSC
                        .requestMatchers("/home").permitAll() // Permitir acesso ao endpoint /home
                        .anyRequest().authenticated()) // Todos os endpoints protegidos
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
