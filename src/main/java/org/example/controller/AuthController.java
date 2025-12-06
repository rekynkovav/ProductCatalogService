package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserDTO;
import org.example.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
     *
     * @param registerRequest DTO с данными для регистрации пользователя.
     * @return ResponseEntity с ответом авторизации (статус 201) или сообщением об ошибке (статус 400).
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO.AuthResponse> registerUser(@Valid @RequestBody UserDTO.RegisterRequest registerRequest) {
        try {
            UserDTO.AuthResponse response = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            UserDTO.AuthResponse errorResponse = new UserDTO.AuthResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Выполняет вход пользователя в систему.
     *
     * @param loginRequest DTO с учетными данными пользователя.
     * @return ResponseEntity с ответом авторизации или сообщением об ошибке (статус 401).
     */
    @PostMapping("/login")
    public ResponseEntity<UserDTO.AuthResponse> login(@Valid @RequestBody UserDTO.LoginRequest loginRequest) {
        try {
            UserDTO.AuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            UserDTO.AuthResponse errorResponse = new UserDTO.AuthResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Выполняет выход пользователя из системы.
     *
     * @param token Токен авторизации, переданный в заголовке запроса.
     * @return ResponseEntity с сообщением о результате операции.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String token) {
        Map<String, String> response = authService.logout(token);
        return ResponseEntity.ok(response);
    }

    /**
     * Проверяет существование пользователя с указанным именем.
     *
     * @param username Имя пользователя для проверки.
     * @return ResponseEntity с результатом проверки существования пользователя.
     */
    @GetMapping("/users/exists/{username}")
    public ResponseEntity<Map<String, Boolean>> userExists(@PathVariable String username) {
        Map<String, Boolean> response = authService.checkUserExists(username);
        return ResponseEntity.ok(response);
    }
}