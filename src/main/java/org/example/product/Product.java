package org.example.product;

import java.util.Objects;

public class Product {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && quantity == product.quantity && Objects.equals(name, product.name);
    }
}
