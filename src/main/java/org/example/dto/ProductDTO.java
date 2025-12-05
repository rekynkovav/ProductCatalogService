package org.example.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDTO {
    private Long id;

    @NotBlank(message = "Название товара обязательно")
    private String name;

    @Min(value = 0, message = "Количество товара не может быть отрицательным")
    private int quantity;

    @Min(value = 0, message = "Цена товара не может быть отрицательной")
    private int price;

    @NotNull(message = "ID категории обязательно")
    private Long categoryId;

    @Data
    public static class CreateProduct {
        @NotBlank(message = "Название товара обязательно")
        private String name;

        @Min(value = 0, message = "Количество товара не может быть отрицательным")
        private int quantity;

        @Min(value = 0, message = "Цена товара не может быть отрицательной")
        private int price;

        @NotNull(message = "ID категории обязательно")
        private Long categoryId;
    }

    @Data
    public static class UpdateProduct {
        @NotBlank(message = "Название товара обязательно")
        private String name;

        @Min(value = 0, message = "Количество товара не может быть отрицательным")
        private int quantity;

        @Min(value = 0, message = "Цена товара не может быть отрицательной")
        private int price;

        @NotNull(message = "ID категории обязательно")
        private Long categoryId;
    }
}