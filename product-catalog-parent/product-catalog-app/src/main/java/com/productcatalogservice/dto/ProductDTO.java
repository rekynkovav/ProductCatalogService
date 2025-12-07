package org.example.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Data Transfer Object (DTO) для товара (Product).
 * Используется для передачи данных между слоями приложения и валидации входных данных.
 *
 * <p>Содержит вложенные классы для операций создания и обновления товара.</p>
 *
 * @see ProductDTO.CreateProduct
 * @see ProductDTO.UpdateProduct
 */
@Data
public class ProductDTO {

    /**
     * Уникальный идентификатор товара. Автоматически генерируется БД.
     */
    private Long id;

    /**
     * Название товара. Обязательное поле.
     */
    @NotBlank(message = "Название товара обязательно")
    private String name;

    /**
     * Количество товара на складе. Не может быть отрицательным.
     */
    @Min(value = 0, message = "Количество товара не может быть отрицательным")
    private int quantity;

    /**
     * Цена товара. Не может быть отрицательной.
     */
    @Min(value = 0, message = "Цена товара не может быть отрицательной")
    private int price;

    /**
     * Идентификатор категории товара. Обязательное поле.
     */
    @NotNull(message = "ID категории обязательно")
    private Long categoryId;

    /**
     * DTO для операции создания товара.
     * Не содержит поля id, так как оно генерируется автоматически.
     */
    @Data
    public static class CreateProduct {

        /**
         * Название товара. Обязательное поле.
         */
        @NotBlank(message = "Название товара обязательно")
        private String name;

        /**
         * Количество товара на складе. Не может быть отрицательным.
         */
        @Min(value = 0, message = "Количество товара не может быть отрицательным")
        private int quantity;

        /**
         * Цена товара. Не может быть отрицательной.
         */
        @Min(value = 0, message = "Цена товара не может быть отрицательной")
        private int price;

        /**
         * Идентификатор категории товара. Обязательное поле.
         */
        @NotNull(message = "ID категории обязательно")
        private Long categoryId;
    }

    /**
     * DTO для операции обновления товара.
     * Не содержит поля id, так как оно передается в URL.
     */
    @Data
    public static class UpdateProduct {

        /**
         * Название товара. Обязательное поле.
         */
        @NotBlank(message = "Название товара обязательно")
        private String name;

        /**
         * Количество товара на складе. Не может быть отрицательным.
         */
        @Min(value = 0, message = "Количество товара не может быть отрицательным")
        private int quantity;

        /**
         * Цена товара. Не может быть отрицательной.
         */
        @Min(value = 0, message = "Цена товара не может быть отрицательной")
        private int price;

        /**
         * Идентификатор категории товара. Обязательное поле.
         */
        @NotNull(message = "ID категории обязательно")
        private Long categoryId;
    }
}