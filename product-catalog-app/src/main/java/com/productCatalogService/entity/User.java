package com.productCatalogService.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Модель пользователя системы.
 * Содержит основную информацию о пользователе и его корзину с товарами.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
     * Ключ - ID товара, значение - количество в корзине.
     * Теперь мы храним только количество, а информацию о товаре берем из каталога.
     */
    @Builder.Default
    private Map<Long, Integer> basket = new HashMap<>();
}