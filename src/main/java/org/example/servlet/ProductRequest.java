package org.example.servlet;

/**
 * DTO класс для запроса создания/обновления товара
 */
public class ProductRequest {
    private String name;
    private int quantity;
    private int price;
    private String category;
    private Long id; // Для обновления

    public ProductRequest() {
    }

    public ProductRequest(String name, int quantity, int price, String category) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ProductRequest{" +
               "name='" + name + '\'' +
               ", quantity=" + quantity +
               ", price=" + price +
               ", category='" + category + '\'' +
               ", id=" + id +
               '}';
    }
}