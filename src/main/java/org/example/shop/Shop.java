package org.example.shop;

import org.example.product.Categories;
import org.example.product.Product;

import java.util.ArrayList;
import java.util.List;

public class Shop {
    private List<Product> productList;

    public Shop() {
        int sizeShop = 50;
        productList = new ArrayList<>(sizeShop);
    }

    public void addProduct (Product product){
        productList.add(product);
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void changeProduct(int id, String name, int quantity, int price, Categories categories){
        productList.get(id).setName(name);
        productList.get(id).setQuantity(quantity);
        productList.get(id).setPrice(price);
        productList.get(id).setCategories(categories);

    }

    public Product searchProduct (long id){
        for (Product product : productList) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }

    public Product searchProduct (String name){
        for (Product product : productList) {
            if (product.getName().equals(name)) {
                return product;
            }
        }
        return null;
    }

    public Product searchProduct (int price){
        for (Product product : productList) {
            if (product.getPrice() == price) {
                return product;
            }
        }
        return null;
    }

    public void deleteProduct (long id){
        for (Product product : productList) {
            if (product.getId() == id) {

            }
        }
    }


}
