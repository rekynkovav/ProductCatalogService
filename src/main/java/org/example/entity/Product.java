package org.example.entity;

import java.io.Serializable;
import java.util.Objects;

public class Product implements Serializable {

    private static final Long serialVersionUID = 1L;
    private long id;
    private String name;
    private int quantity;
    private int price;
    private Categories categories;

    public Product(long id, String name, int quantity, int price, Categories categories) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.categories = categories;
    }

    public Product() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCategories(Categories categories) {
        this.categories = categories;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && quantity == product.quantity && price == product.price && Objects.equals(name, product.name) && categories == product.categories;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, quantity, price, categories);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", categories=" + categories +
                '}';
    }
}
