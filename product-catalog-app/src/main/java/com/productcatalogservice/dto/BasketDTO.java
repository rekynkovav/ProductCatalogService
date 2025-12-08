package com.productcatalogservice.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.Map;

/**
 * Data Transfer Object (DTO) для корзины покупок пользователя.
 * Представляет собой коллекцию товаров с количеством каждого товара.
 *
 * <p>Содержит вложенные классы для представления товара в корзине и запроса добавления товара.</p>
 *
 * @see BasketDTO.ProductItem
 * @see BasketDTO.AddToBasketRequest
 */
@Data
public class BasketDTO {

    /**
     * Карта товаров в корзине. Ключ - ID товара, значение - информация о товаре и количестве.
     */
    private Map<Long, ProductItem> items;

    /**
     * DTO для представления товара в корзине.
     * Содержит информацию о товаре и количестве в корзине.
     */
    @Data
    public static class ProductItem {

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
    }

    /**
     * DTO для запроса добавления товара в корзину.
     * Используется в API для указания количества добавляемого товара.
     */
    @Data
    public static class AddToBasketRequest {

        /**
         * Количество товара для добавления в корзину. Должно быть положительным числом.
         */
        @Min(value = 1, message = "Количество должно быть положительным")
        private int quantity;
    }
}