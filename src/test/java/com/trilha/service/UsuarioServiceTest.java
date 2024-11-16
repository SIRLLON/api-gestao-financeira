package com.trilha.service;

import com.trilha.exception.UsuarioNotFoundException;
import com.trilha.model.Usuario;
import com.trilha.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario("Teste Nome", "teste@email.com", "senha123");
    }

    @Test
    void whenSaveUser_thenReturnSavedUser() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        Usuario savedUser = usuarioService.saveUser(usuario);
        assertThat(savedUser).isEqualTo(usuario);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void whenGetAllUsers_thenReturnUserList() {
        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(usuario));
        List<Usuario> users = usuarioService.getAllUsers();
        assertThat(users).hasSize(1).contains(usuario);
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void whenGetUserById_thenReturnUser() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        Optional<Usuario> foundUser = usuarioService.getUserById(usuario.getId());
        assertThat(foundUser).isPresent().contains(usuario);
        verify(usuarioRepository, times(1)).findById(usuario.getId());
    }

    @Test
    void whenUpdateUser_thenReturnUpdatedUser() {
        when(usuarioRepository.existsById(usuario.getId())).thenReturn(true);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        usuario.setNome("Nome Atualizado");
        Optional<Usuario> updatedUser = usuarioService.updateUser(usuario.getId(), usuario);
        assertThat(updatedUser).isPresent().contains(usuario);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void whenUpdateNonExistentUser_thenReturnEmpty() {
        when(usuarioRepository.existsById(usuario.getId())).thenReturn(false);
        Optional<Usuario> updatedUser = usuarioService.updateUser(usuario.getId(), usuario);
        assertThat(updatedUser).isNotPresent();
        verify(usuarioRepository, times(0)).save(usuario);
    }

    @Test
    void whenDeleteUser_thenUserIsDeleted() {
        when(usuarioRepository.existsById(usuario.getId())).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(usuario.getId());
        usuarioService.deleteUser(usuario.getId());
        verify(usuarioRepository, times(1)).deleteById(usuario.getId());
    }

    @Test
    void whenDeleteNonExistentUser_thenThrowException() {
        when(usuarioRepository.existsById(usuario.getId())).thenReturn(false);
        assertThatThrownBy(() -> usuarioService.deleteUser(usuario.getId()))
                .isInstanceOf(UsuarioNotFoundException.class)
                .hasMessage("Usuário com ID " + usuario.getId() + " não encontrado.");
        verify(usuarioRepository, times(0)).deleteById(usuario.getId());
    }
}