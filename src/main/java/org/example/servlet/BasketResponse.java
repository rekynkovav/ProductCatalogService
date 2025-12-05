package org.example.servlet;

import org.example.model.entity.Product;

import java.util.Map;

/**
 * DTO класс для ответа с корзиной пользователя
 */
public class BasketResponse {
    private boolean success;
    private String message;
    private Map<Long, Product> basketItems;
    private double totalAmount;

    public BasketResponse() {
    }

    public BasketResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public BasketResponse(boolean success, String message, Map<Long, Product> basketItems, double totalAmount) {
        this.success = success;
        this.message = message;
        this.basketItems = basketItems;
        this.totalAmount = totalAmount;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<Long, Product> getBasketItems() {
        return basketItems;
    }

    public void setBasketItems(Map<Long, Product> basketItems) {
        this.basketItems = basketItems;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}