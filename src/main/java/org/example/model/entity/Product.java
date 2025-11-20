package org.example.model.entity;

import java.util.Objects;

/**
 * Модель товара магазина.
 * Представляет продукт с основными характеристиками: название, количество, цена, категория.
 * Реализует интерфейс Serializable для поддержки сериализации.
 */
public class Product{

    /**
     * Уникальный идентификатор товара.
     */
    private Long id;

    /**
     * Название товара.
     */
    private String name;

    /**
     * Количество товара в наличии.
     */
    private int quantity;

    /**
     * Цена товара.
     */
    private int price;

    /**
     * Категория товара.
     */
    private Category category;

    /**
     * Конструктор по умолчанию.
     */
    public Product() {
    }

    /**
     * Конструктор с параметрами.
     *
     * @param name     название товара
     * @param quantity количество товара
     * @param price    цена товара
     * @param category категория товара
     */
    public Product(String name, int quantity, int price, Category category) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
    }

    /**
     * Возвращает идентификатор товара.
     *
     * @return идентификатор товара
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор товара.
     *
     * @param id идентификатор товара
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает название товара.
     *
     * @return название товара
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает название товара.
     *
     * @param name название товара
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает количество товара.
     *
     * @return количество товара
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Устанавливает количество товара.
     *
     * @param quantity количество товара
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Возвращает цену товара.
     *
     * @return цена товара
     */
    public int getPrice() {
        return price;
    }

    /**
     * Устанавливает цену товара.
     *
     * @param price цена товара
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Возвращает категорию товара.
     *
     * @return категория товара
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Устанавливает категорию товара.
     *
     * @param category категория товара
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Уменьшает количество товара на указанное значение.
     *
     * @param amount количество для вычитания
     */
    public void subtractQuantity(int amount) {
        this.quantity -= amount;
    }

    /**
     * Увеличивает количество товара на указанное значение.
     *
     * @param amount количество для добавления
     */
    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, quantity, price, category);
    }

    /**
     * Возвращает строковое представление товара.
     *
     * @return строка с информацией о товаре
     */
    @Override
    public String toString() {
        return name +
               ", количество: " + quantity + " шт" +
               ", цена: " + price;
    }
}