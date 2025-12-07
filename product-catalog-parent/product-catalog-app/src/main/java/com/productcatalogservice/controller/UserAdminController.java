package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserDTO;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Контроллер для административного управления пользователями.
 * Предоставляет REST API для получения списка всех пользователей.
 * Все операции требуют наличия валидного токена авторизации в заголовке запроса.
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserService userService;

    /**
     * Получает список всех пользователей системы.
     *
     * @param token Токен авторизации, переданный в заголовке запроса.
     * @return ResponseEntity со списком всех пользователей или сообщением об ошибке (статус 403).
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        try {
            List<UserDTO> users = userService.getAllUsersForAdmin(token);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}