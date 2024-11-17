package com.trilha.service;

import com.trilha.dto.ExternalAccountResponse;
import com.trilha.dto.UsuarioRequest;
import com.trilha.exception.UsuarioNotFoundException;
import com.trilha.model.Usuario;
import com.trilha.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ExternalAccountService externalAccountService;

    public Map<String, Object> createUserWithAccount(UsuarioRequest usuarioRequest) {
        // Criar o usuário no banco de dados
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioRequest.getNome());
        usuario.setEmail(usuarioRequest.getEmail());
        usuario.setSenha(usuarioRequest.getSenha());
        usuario = usuarioRepository.save(usuario);

        // Obter os dados da API externa
        ExternalAccountResponse externalAccount = externalAccountService.getAccountData(usuario.getId());

        // Montar a resposta
        Map<String, Object> response = new HashMap<>();
        response.put("id", usuario.getId());
        response.put("name", usuario.getNome());
        response.put("email", usuario.getEmail());
        response.put("accountNumber", externalAccount.getAccountNumber());
        response.put("balance", externalAccount.getBalance());

        return response;
    }

    public Usuario createUser(UsuarioRequest usuarioRequest) {
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioRequest.getNome());
        usuario.setEmail(usuarioRequest.getEmail());
        usuario.setSenha(usuarioRequest.getSenha());
        return usuarioRepository.save(usuario); // Salva no banco de dados
    }

    public List<Usuario> getAllUsers() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getUserById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> updateUser(Long id, Usuario usuario) {
        if (usuarioRepository.existsById(id)) {
            usuario.setId(id);
            return Optional.of(usuarioRepository.save(usuario));
        }
        return Optional.empty();
    }

    public void deleteUser(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNotFoundException("Usuário com ID " + id + " não encontrado.");
        }
        usuarioRepository.deleteById(id);
    }

    public String importUsersFromExcel(MultipartFile file, ExcelImportService excelImportService) {
        try {
            excelImportService.importUsersFromExcel(file);
            return "Users imported successfully!";
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while processing the file.", e);
        }
    }
}
