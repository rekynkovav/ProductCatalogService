package org.example.shop;

import org.example.entity.Categories;
import org.example.entity.Product;

import java.util.HashMap;
import java.util.Map;

public class Shop {
    private HashMap<Long,Product> productMap;

    public Shop() {
        int sizeShop = 50;
        productMap = new HashMap<>(sizeShop);
    }

    public void addProduct (Product product){
        productMap.put(product.getId(), product);
    }

    public void changeProduct(long id, String name, int quantity, int price, Categories categories){
        productMap.get(id).setName(name);
        productMap.get(id).setQuantity(quantity);
        productMap.get(id).setPrice(price);
        productMap.get(id).setCategories(categories);
    }

    public void deleteProduct (long id){
        productMap.remove(id);
    }

    public String showProductId (long id){
        return productMap.get(id).toString();
    }

    public HashMap<Long, Product> getProductMap() {
        return productMap;
    }

    public void setProductMap(HashMap<Long, Product> productMap) {
        this.productMap = productMap;
    }

    public void showAllProduct (){
       for(Map.Entry<Long, Product> product: productMap.entrySet()){
           System.out.println(product.getKey() + " "
                   + product.getValue().getName() + " цена: "
                   + product.getValue().getPrice() + " доступно: "
                   + product.getValue().getQuantity() + " шт."
           );
       }
    }

    public String searchProduct (String name){
        return null;
    }
}
