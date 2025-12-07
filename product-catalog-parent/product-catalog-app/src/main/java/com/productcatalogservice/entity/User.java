package org.example.model.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Модель пользователя системы.
 * Содержит основную информацию о пользователе и его корзину с товарами.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {
    /**
     * Уникальный идентификатор пользователя.
     */
    private Long id;

    /**
     * Имя пользователя для входа в систему.
     */
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    private String userName;

    /**
     * Пароль пользователя.
     */
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 4, message = "Пароль должен содержать минимум 4 символа")
    private String password;

    /**
     * Роль пользователя в системе.
     */
    private Role role;

    /**
     * Корзина товаров пользователя.
     * Ключ - идентификатор товара, значение - товар с количеством.
     */
    @Builder.Default
    private Map<Long, Product> mapBasket = new HashMap<>();
}