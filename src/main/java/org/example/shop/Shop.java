package org.example.shop;

import org.example.product.Categories;
import org.example.product.Product;

import java.util.HashMap;
import java.util.Map;

public class Shop {
    private Map<Long,Product> productMap;

    public Shop() {
        int sizeShop = 50;
        productMap = new HashMap<>(sizeShop);
    }

    public void addProduct (Product product){
        productMap.put(product.getId(), product);
    }

    public void changeProduct(int id, String name, int quantity, int price, Categories categories){
        productMap.get(id).setName(name);
        productMap.get(id).setQuantity(quantity);
        productMap.get(id).setPrice(price);
        productMap.get(id).setCategories(categories);

    }

    public void deleteProduct (long id){
        productMap.remove(id);
    }

    public String showProduct (long id){
        return productMap.get(id).toString();
    }

    public String searchProduct (String name){
        return null;
    }
}
