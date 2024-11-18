package com.trilha.service;

import com.trilha.dto.ExternalAccountResponse;
import com.trilha.dto.UsuarioRequest;
import com.trilha.dto.UsuarioResponse;
import com.trilha.exception.UsuarioNotFoundException;
import com.trilha.mapper.UsuarioMapper;
import com.trilha.model.Usuario;
import com.trilha.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService {

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ExternalAccountService externalAccountService;

    public Map<String, Object> createUserWithAccount(UsuarioRequest usuarioRequest) {
        // Criar o usuário no banco de dados
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioRequest.getNome());
        usuario.setEmail(usuarioRequest.getEmail());
        usuario.setSenha(passwordEncoder.encode(usuarioRequest.getSenha())); // Criptografando a senha
        usuario = usuarioRepository.save(usuario);

        // Obter os dados da API externa
        ExternalAccountResponse externalAccount = externalAccountService.getAccountData(usuario.getId());

        // Atualizar o saldo e o número da conta no banco de dados
        usuario.setSaldo(externalAccount.getBalance());
        usuario.setAccountNumber(externalAccount.getAccountNumber());
        usuarioRepository.save(usuario); // Salvar no banco

        // Montar a resposta formatada
        UsuarioResponse usuarioResponse = UsuarioMapper.toUsuarioResponse(usuario, externalAccount);


        // Criar a resposta no formato Map
        Map<String, Object> response = new HashMap<>();
        response.put("id", usuarioResponse.getId());
        response.put("name", usuarioResponse.getNome());
        response.put("email", usuarioResponse.getEmail());
        response.put("accountNumber", usuarioResponse.getAccountNumber());
        response.put("balance", usuarioResponse.getBalance());

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
