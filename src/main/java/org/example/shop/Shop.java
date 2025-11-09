package org.example.shop;

import org.example.entity.Categories;
import org.example.entity.Product;
import org.example.service.BusinessInfo;
import org.example.service.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * класс Магазин хранящий в себе список товаров магазина
 * метод addProduct дял добавления новых товаров в магазин
 * метод changeProduct для изменения товара
 * метод deleteProduct для удаления товара
 * метод showAllProduct показать все товары в магазине
 * метод searchCategories фильтрующий через Stream по категориям товаров
 * метод addBasket для добавления товара в корзину покупателя, если товар уже есть то прибавляется количество товара
 * с проверкой, что количество запрашиваемого товара есть в наличие в магазине
 */
public class Shop {

    public static HashMap<Long, Product> productMap;

    public Shop() {
        int sizeShop = 50;
        productMap = new HashMap<>(sizeShop);
    }

    public void addProduct(Product product) {
        productMap.put(Product.id, product);
    }

    public void changeProduct(long id, String name, int quantity, int price, Categories categories) {
        productMap.get(id).setName(name);
        productMap.get(id).setQuantity(quantity);
        productMap.get(id).setPrice(price);
        productMap.get(id).setCategories(categories);
    }

    public void deleteProduct(long id) {
        productMap.remove(id);
    }

    public void showAllProduct() {
        for (Map.Entry<Long, Product> product : productMap.entrySet()) {
            System.out.println(product.getKey() + " "
                               + product.getValue().getName() + " цена: "
                               + product.getValue().getPrice() + " доступно: "
                               + product.getValue().getQuantity() + " шт."
            );
        }
    }

    public void searchCategories(Categories categories) {
        List<Product> listProduct = productMap.values()
                .stream()
                .filter(product -> product.getCategories().equals(categories))
                .toList();

        if (!listProduct.isEmpty()) {
            listProduct.forEach(System.out::println);
        }
        if(listProduct.isEmpty()) {
            System.out.println("В данной категории нет товаров");
        }
    }

    public String addBasket(long id, int quantity) {
        String temp = null;
        int numberOfRequests = 0;
        if (quantity <= productMap.get(id).getQuantity()) {
            if (Service.getThisUser().getMapBasket().containsKey(id)) {
                productMap.get(id).setQuantity(productMap.get(id).getQuantity() - quantity);
                Service.getThisUser().getMapBasket().get(id).setQuantity(Service.getThisUser().getMapBasket().get(id).getQuantity() + quantity);
                numberOfRequests++;
                BusinessInfo.getRequestProductsMap().put(id, new Product(productMap.get(id).getName(), numberOfRequests, productMap.get(id).getPrice(), productMap.get(id).getCategories()));
                temp = "товары успешно добавлены в корзину \n";
            } if (!Service.getThisUser().getMapBasket().containsKey(id)) {
                productMap.get(id).setQuantity(productMap.get(id).getQuantity() - quantity);
                Service.getThisUser().getMapBasket().put(id, new Product(productMap.get(id).getName(), quantity, productMap.get(id).getPrice(), productMap.get(id).getCategories()));
                numberOfRequests++;
                BusinessInfo.getRequestProductsMap().put(id, new Product(productMap.get(id).getName(), numberOfRequests, productMap.get(id).getPrice(), productMap.get(id).getCategories()));
                temp = "товары успешно добавлены в корзину \n";
            }
        } else {
            temp = "вы можете купить только " + productMap.get(id).getQuantity() + "\n";
        }
        return temp;
    }

    public HashMap<Long, Product> getProductMap() {
        return productMap;
    }

    public void setProductMap(HashMap<Long, Product> productMap) {
        this.productMap = productMap;
    }
}
