package org.example.config;

import org.example.model.entity.Role;
import org.example.model.entity.User;

/**
 * Интерфейс конфигурации безопасности пользователей.
 * Определяет методы для аутентификации и регистрации пользователей.
 */
public interface UserSecurityConfig {

    /**
     * Проверяет учетные данные пользователя.
     *
     * @param userName имя пользователя
     * @param password пароль пользователя
     * @return true если аутентификация успешна, иначе false
     */
    boolean verificationUser(String userName, String password);

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param userName имя пользователя
     * @param password пароль пользователя
     * @param role     роль пользователя
     */
    void registerUser(String userName, String password, Role role);

    /**
     * Устанавливает текущего аутентифицированного пользователя.
     *
     * @param thisUser объект пользователя
     */
    void setThisUser(User thisUser);

    /**
     * Проверяет аутентифицирован ли текущий пользователь.
     *
     * @return true если пользователь аутентифицирован, иначе false
     */
    boolean isAuthenticated();

    /**
     * Сбрасывает состояние конфигурации безопасности (для тестирования).
     */
    void reset();
}