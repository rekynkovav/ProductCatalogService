package com.productCatalogService.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Модель категории товаров.
 * <p>
 * Представляет группу товаров с общими характеристиками.
 * Категории используются для организации каталога товаров,
 * фильтрации и навигации. Каждая категория имеет уникальное имя.
 * </p>
 *
 * <h3>Особенности:</h3>
 * <ul>
 *   <li>Категория может содержать множество товаров</li>
 *   <li>Название категории должно быть уникальным</li>
 *   <li>Категории могут использоваться для построения дерева (при необходимости)</li>
 * </ul>
 *
 * @see Product
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
public class Category {

    /**
     * Уникальный идентификатор категории.
     * <p>
     * Не может быть {@code null} для сохранённых категорий.
     * </p>
     */
    @NotNull(message = "ID категории обязательно")
    private Long id;

    /**
     * Название категории.
     * <p>
     * Должно быть уникальным в пределах системы.
     * Используется для отображения и поиска категорий.
     * </p>
     */
    @NotBlank(message = "Название категории обязательно")
    private String name;

    /**
     * Создает новую категорию с указанным названием.
     * <p>
     * Конструктор используется для создания категории перед сохранением.
     * Идентификатор устанавливается в {@code null} и будет сгенерирован
     * при сохранении в базу данных.
     * </p>
     *
     * @param name название категории (не может быть {@code null} или пустым)
     * @throws IllegalArgumentException если {@code name} равен {@code null} или пуст
     */
    public Category(String name) {
        this.name = name;
    }
}