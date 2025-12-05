package org.example.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.entity.Role;

@Data
public class UserDTO {
    private Long id;

    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    private String userName;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 4, message = "Пароль должен содержать минимум 4 символа")
    private String password;

    private Role role;

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Имя пользователя обязательно")
        private String username;

        @NotBlank(message = "Пароль обязателен")
        private String password;
    }

    @Data
    public static class AuthResponse {
        private String message;
        private String token;
        private UserInfo user;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class UserInfo {
        private Long id;
        private String username;
        private Role role;
    }
}