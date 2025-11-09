package org.example.shop;

import org.example.entity.Categories;
import org.example.entity.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shop {
    public static HashMap<Long,Product> productMap;
    private ArrayList<Product> listProduct;

    public Shop() {
        int sizeShop = 50;
        productMap = new HashMap<>(sizeShop);
    }

    public void addProduct (Product product){
        productMap.put(Product.id, product);
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

    public void searchCategories (Categories categories){
        List<Product> listProduct = productMap.values()
                .stream()
                .filter(product -> product.getCategories().equals(categories))
                .toList();

        if(!listProduct.isEmpty()){
            listProduct.forEach(System.out::println);
        }else{
            System.out.println("В данной категории нет товаров");
        }
    }

    public void addBasket (long id, int quantity){
        /// //
    }
}
