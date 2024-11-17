package com.trilha.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<String> handleUsuarioNotFound(UsuarioNotFoundException ex) {
        // Personalize a resposta para o erro de usuário não encontrado
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado: " + ex.getMessage());
    }

    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<String> handleCategoriaNotFound(CategoriaNotFoundException ex) {
        // Personalize a resposta para o erro de categoria não encontrada
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoria não encontrada: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        // Resposta padrão para exceções genéricas
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno: " + ex.getMessage());
    }
}

