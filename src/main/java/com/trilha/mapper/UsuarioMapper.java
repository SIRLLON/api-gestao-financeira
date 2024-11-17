package com.trilha.mapper;

import com.trilha.dto.UsuarioResponse;
import com.trilha.model.Usuario;

public class UsuarioMapper {

    public static UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
