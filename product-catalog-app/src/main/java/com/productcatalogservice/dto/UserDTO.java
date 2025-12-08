package com.productcatalogservice.dto;

import com.productcatalogservice.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) для пользователя (User).
 * Используется для передачи данных пользователя между слоями приложения.
 *
 * <p>Содержит вложенные классы для регистрации, аутентификации и представления
 * информации о пользователе.</p>
 *
 * @see UserDTO.RegisterRequest
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

    /** Пароль пользователя. Обязательное поле, минимум 6 символов. */
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    private String password;

    /** Роль пользователя в системе. Определяет уровень доступа. */
    private Role role;

    /**
     * DTO для запроса регистрации нового пользователя.
     * Содержит все необходимые данные для создания учетной записи.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {

        /** Имя пользователя (логин). Обязательное поле, от 3 до 50 символов. */
        @NotBlank(message = "Имя пользователя обязательно")
        @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
        private String userName;

        /** Пароль пользователя. Обязательное поле, минимум 6 символов. */
        @NotBlank(message = "Пароль обязателен")
        @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
        private String password;

        /** Роль пользователя. Если не указана, устанавливается роль по умолчанию. */
        private Role role;
    }

    /**
     * DTO для запроса аутентификации пользователя.
     * Содержит учетные данные для входа в систему.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {

        /** Имя пользователя для входа в систему. Обязательное поле. */
        @NotBlank(message = "Имя пользователя обязательно")
        private String username;

        /** Пароль пользователя для входа в систему. Обязательное поле. */
        @NotBlank(message = "Пароль обязателен")
        private String password;
    }

    /**
     * DTO для ответа после успешной аутентификации или регистрации.
     * Содержит сообщение, JWT токен и информацию о пользователе.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {

        /** Сообщение об успешной операции (аутентификации или регистрации). */
        private String message;

        /** JWT токен для последующих авторизованных запросов. */
        private String token;

        /** Информация об аутентифицированном пользователе. */
        private UserInfo user;
    }

    /**
     * DTO для обновления информации о пользователе.
     * Используется при редактировании профиля пользователя.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserRequest {

        /** Имя пользователя. Необязательное поле для обновления. */
        @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
        private String userName;

        /** Текущий пароль для подтверждения изменений. Обязателен при смене пароля. */
        private String currentPassword;

        /**
         * DTO для представления базовой информации о пользователе.
         * Используется в ответах API, где не нужна полная информация (например, без пароля).
         */


        /** Новый пароль. Должен содержать минимум 6 символов. */
        @Size(min = 6, message = "Новый пароль должен содержать минимум 6 символов")
        private String newPassword;

        /** Новая роль пользователя. Только для администраторов. */
        private Role role;
    }

    /**
     * DTO для представления публичной информации о пользователе.
     * Используется когда нужно показать информацию о пользователе другим пользователям.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PublicUserInfo {

        /** Уникальный идентификатор пользователя. */
        private Long id;

        /** Имя пользователя (логин). */
        private String username;

        /** Роль пользователя в системе. */
        private Role role;
    }

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserInfo {

        /** Уникальный идентификатор пользователя. */
        private Long id;

        /** Имя пользователя (логин). */
        private String username;

        /** Роль пользователя в системе. */
        private Role role;
    }
}