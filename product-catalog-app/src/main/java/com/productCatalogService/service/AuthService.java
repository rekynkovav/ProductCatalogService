package com.productCatalogService.service;

import com.productCatalogService.dto.UserDTO;
import java.util.Map;

/**
 * Сервис для управления аутентификацией и регистрацией пользователей
 */
public interface AuthService {

    /**
     * Регистрация нового пользователя
     */
    UserDTO.AuthResponse register(UserDTO.RegisterRequest request);

    /**
     * Аутентификация пользователя
     */
    UserDTO.AuthResponse login(UserDTO.LoginRequest request);

    /**
     * Выход пользователя из системы
     */
    Map<String, String> logout(String token);

    /**
     * Проверка существования пользователя
     */
    Map<String, Boolean> checkUserExists(String username);

    /**
     * Валидация токена
     */
    UserDTO.UserInfo validateToken(String token);

    /**
     * Обновление токена
     */
    UserDTO.AuthResponse refreshToken(String oldToken);

}