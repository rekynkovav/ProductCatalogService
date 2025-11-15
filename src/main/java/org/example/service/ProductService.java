package org.example.service;

import org.example.model.entity.Categories;
import org.example.model.entity.Product;

public interface ProductService {
    void addProduct(Product product);

    void modificationProduct(long id, String name, int quantity, int price, Categories categories);

    void deleteProduct(long id);

    void showAllProduct();

    void searchCategories(Categories categories);

    void addBasket(long id, int quantity);

    boolean checkingBasketForProduct(long id);

    void saveAllMapFromDB();

    void loadAllMapFromDB();
}
