package com.productCatalogService.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class BasketDTO {
    /**
     * Карта товаров в корзине.
     * Ключ - ID товара, значение - информация о товаре и количестве в корзине.
     */
    private Map<Long, BasketItemDTO> items;

    /**
     * Общее количество позиций в корзине.
     */
    private int totalItems;

    /**
     * Общая стоимость корзины.
     */
    private int totalPrice;

    @Data
    public static class BasketItemDTO {
        /**
         * Уникальный идентификатор товара.
         */
        private Long id;

        /**
         * Название товара.
         */
        private String name;

        /**
         * Количество данного товара в корзине.
         */
        private int quantity;

        /**
         * Цена товара за единицу.
         */
        private int price;

        /**
         * Идентификатор категории товара.
         */
        private Long categoryId;

        /**
         * Доступное количество на складе.
         */
        private int stockQuantity;

        /**
         * Общая стоимость позиции (цена * количество).
         */
        private int itemTotal;

        /**
         * Доступен ли товар для заказа (хватает ли на складе).
         */
        private boolean available;
    }

    @Data
    public static class AddToBasketRequest {
        /**
         * Количество товара для добавления в корзину. Должно быть положительным числом.
         */
        @Min(value = 1, message = "Количество должно быть положительным")
        private int quantity;
    }

    @Data
    public static class BasketSummary {
        private int itemCount;
        private int totalQuantity;
        private int totalPrice;
    }

    @Data
    public static class UpdateBasketItemRequest {
        @NotNull(message = "Количество обязательно")
        @Min(value = 0, message = "Количество не может быть отрицательным")
        private Integer quantity;
    }
}