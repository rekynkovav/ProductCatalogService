package com.productCatalogService.controller;

import com.productCatalogService.dto.UserDTO;
import com.productCatalogService.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Контроллер для аутентификации и регистрации пользователей.
 * Предоставляет REST API для регистрации, входа, выхода и проверки существования пользователей.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Регистрирует нового пользователя в системе.
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO.AuthResponse> registerUser(@Valid @RequestBody UserDTO.RegisterRequest registerRequest) {
        UserDTO.AuthResponse response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Выполняет вход пользователя в систему.
     */
    @PostMapping("/login")
    public ResponseEntity<UserDTO.AuthResponse> login(@Valid @RequestBody UserDTO.LoginRequest loginRequest) {
        UserDTO.AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Выполняет выход пользователя из системы.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String token) {
        Map<String, String> response = authService.logout(token);
        return ResponseEntity.ok(response);
    }

    /**
     * Проверяет существование пользователя с указанным именем.
     */
    @GetMapping("/users/exists/{username}")
    public ResponseEntity<Map<String, Boolean>> userExists(@PathVariable String username) {
        Map<String, Boolean> response = authService.checkUserExists(username);
        return ResponseEntity.ok(response);
    }
}