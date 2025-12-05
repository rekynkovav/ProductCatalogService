package org.example.dto;

import lombok.Data;

import java.util.Map;

@Data
public class BasketDTO {
    private Map<Long, ProductItem> items;

    @Data
    public static class ProductItem {
        private Long id;
        private String name;
        private int quantity;
        private int price;
        private Long categoryId;
    }

    @Data
    public static class AddToBasketRequest {
        @javax.validation.constraints.Min(value = 1, message = "Количество должно быть положительным")
        private int quantity;
    }
}