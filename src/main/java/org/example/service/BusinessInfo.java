package org.example.service;

import org.example.entity.Product;
import org.example.entity.User;

import java.io.Serializable;
import java.util.HashMap;

/**
 * класс содержаший 2 мапы в которые сохраняется сериализуется и десериализуется информация по действиям всех пользователей а именно:
 * добавление удаление изменение товаров добавление товаров в корзину
 */
public class BusinessInfo implements Serializable {
    private static HashMap<Long, Product> requestProducts;
    private static HashMap<String, User> requestUser;

    static {
        requestProducts = new HashMap<>(50);
        requestUser = new HashMap<>(50);
    }

    public static void setRequestProducts(HashMap<Long, Product> requestProducts) {
        BusinessInfo.requestProducts = requestProducts;
    }

    public static HashMap<Long, Product> getRequestProductsMap() {
        return requestProducts;
    }

    public static HashMap<String, User> getRequestUserMap() {
        return requestUser;
    }

    public static void setRequestUser(HashMap<String, User> requestUser) {
        BusinessInfo.requestUser = requestUser;
    }
}
