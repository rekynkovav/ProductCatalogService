package org.example.servlet;

/**
 * DTO класс для запроса добавления товара в корзину
 */
public class BasketRequest {
    private Long productId;
    private int quantity;

    public BasketRequest() {
    }

    public BasketRequest(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "BasketRequest{" +
               "productId=" + productId +
               ", quantity=" + quantity +
               '}';
    }
}
