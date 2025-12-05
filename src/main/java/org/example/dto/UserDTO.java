package org.example.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.entity.Role;

/**
 * Data Transfer Object (DTO) для пользователя (User).
 * Используется для передачи данных пользователя между слоями приложения.
 *
 * <p>Содержит вложенные классы для аутентификации и представления информации о пользователе.</p>
 *
 * @see UserDTO.LoginRequest
 * @see UserDTO.AuthResponse
 * @see UserDTO.UserInfo
 */
@Data
public class UserDTO {

    /** Уникальный идентификатор пользователя. Автоматически генерируется БД. */
    private Long id;

    /** Имя пользователя (логин). Обязательное поле, от 3 до 50 символов. */
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    private String userName;

    /** Пароль пользователя. Обязательное поле, минимум 4 символа. */
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 4, message = "Пароль должен содержать минимум 4 символа")
    private String password;

    /** Роль пользователя в системе. Определяет уровень доступа. */
    private Role role;

    /**
     * DTO для запроса аутентификации пользователя.
     * Содержит учетные данные для входа в систему.
     */
    @Data
    public static class LoginRequest {

        /** Имя пользователя для входа в систему. Обязательное поле. */
        @NotBlank(message = "Имя пользователя обязательно")
        private String username;

        /** Пароль пользователя для входа в систему. Обязательное поле. */
        @NotBlank(message = "Пароль обязателен")
        private String password;
    }

    /**
     * DTO для ответа после успешной аутентификации.
     * Содержает сообщение, токен и информацию о пользователе.
     */
    @Data
    public static class AuthResponse {

        /** Сообщение об успешной аутентификации. */
        private String message;

        /** JWT токен для последующих запросов. */
        private String token;

        /** Информация об аутентифицированном пользователе. */
        private UserInfo user;
    }

    /**
     * DTO для представления базовой информации о пользователе.
     * Используется в ответах API, где не нужна полная информация.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class UserInfo {

        /** Уникальный идентификатор пользователя. */
        private Long id;

        /** Имя пользователя (логин). */
        private String username;

        /** Роль пользователя в системе. */
        private Role role;
    }
}