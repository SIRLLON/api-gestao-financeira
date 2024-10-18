package com.trilha.service;

import com.trilha.exception.UsuarioNotFoundException;
import com.trilha.model.BankBalance;
import com.trilha.model.Usuario;
import com.trilha.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final RestTemplate restTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BankBalance getBankBalance(String userId) {
        String url = "https://66fada9c8583ac93b40a2cc3.mockapi.io/saldos?userId=" + userId;
        BankBalance[] balances = restTemplate.getForObject(url, BankBalance[].class);

        if (balances != null && balances.length > 0) {
            return balances[0];  // Retorna o primeiro saldo da lista
        } else {
            throw new RuntimeException("No bank balance found for user ID: " + userId);
        }
    }

    public Usuario saveUser(Usuario usuario) {

        // Aqui você cria o usuário
        Usuario createdUsuario = usuarioRepository.save(usuario);

        // Após criar o usuário, captura os dados bancários
        BankBalance bankBalance = getBankBalance(createdUsuario.getId().toString());

        // Agora você pode associar esses dados bancários ao usuário ou armazená-los
        createdUsuario.setBankBalance(bankBalance);
        usuarioRepository.save(createdUsuario);  // Atualiza o usuário com o saldo

        return createdUsuario;
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
