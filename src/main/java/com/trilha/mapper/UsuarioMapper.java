package com.trilha.mapper;

import com.trilha.dto.ExternalAccountResponse;
import com.trilha.dto.UsuarioResponse;
import com.trilha.model.Usuario;

public class UsuarioMapper {

    public static UsuarioResponse toUsuarioResponse(Usuario usuario, ExternalAccountResponse externalAccount) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                externalAccount != null ? externalAccount.getAccountNumber() : null, // Formata número da conta se disponível
                usuario.getSaldo() // Formata o saldo
        );
    }
}

