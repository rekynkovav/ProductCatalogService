package org.example.model.entity;

import org.example.repository.impl.UserRepositoryImpl;

import java.io.Serializable;
import java.util.Objects;

/**
 * модель товара магазина
 * поля
 * номер товара
 * название
 * количество товара
 * цена
 * категория
 */
public class Product implements Serializable {

    private static final long SerialVersionUID = 1;
    private static long id;
    private String name;
    private int quantity;
    private int price;
    private Categories categories;

    public Product(String name, int quantity, int price, Categories categories) {
        id++;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.categories = categories;
    }

    static {
        id = UserRepositoryImpl.getInstance().getUserMap().size();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    /**
     * метод для вычитания количества товара при покупке
     */
    public void subtractQuantity(int temp) {
        quantity = quantity - temp;
    }

    public void appendQuantity(int temp) {
        quantity = quantity + temp;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCategories(Categories categories) {
        this.categories = categories;
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

    public Categories getCategories() {
        return categories;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return quantity == product.quantity && price == product.price && Objects.equals(name, product.name) && categories == product.categories;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, quantity, price, categories);
    }

    @Override
    public String toString() {
        return name +
                ", количество: " + quantity + " шт" +
                ", цена: " + price;
    }
}
