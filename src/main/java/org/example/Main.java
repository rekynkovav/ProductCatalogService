package org.example;


import org.example.entity.Categories;
import org.example.entity.Product;
import org.example.entity.Role;
import org.example.front.Front;
import org.example.service.Service;
import org.example.shop.Shop;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String userName;
        String password;
        Shop shop = new Shop();
        Front front = new Front();
        shop.setProductMap(Service.loadProduct());
        front.setUserMap(Service.loadUser());
        Scanner scanner = new Scanner(System.in);

//        shop.addProduct(new Product(1, "хлеб", 70, 65, Categories.food));
//        shop.addProduct(new Product(2, "масло", 5, 317, Categories.food));
//        shop.addProduct(new Product(3, "NOKIA 1100", 7, 1200, Categories.electronics));
//        shop.addProduct(new Product(4, "плоскогубцы", 12, 362, Categories.tools));
//        shop.addProduct(new Product(5, "куртка", 2, 6500, Categories.clothes));
//        shop.addProduct(new Product(6, "удочка", 6, 650, Categories.other));
        System.out.println("Добро пожаловать на маркетплейс, к покупке доступны:");
        shop.showAllProduct();
        System.out.println("Для покупки товаров, Авторизуйтесь или Зарегистрируйтесь");
        do {
            System.out.print("Логин: ");
            userName = scanner.nextLine();
            if (front.getUserMap().containsKey(userName)) {
                System.out.print("Пароль: ");
                password = scanner.nextLine();
                front.checkUser(userName, password);
                startApplication();
            }
            System.out.print("Пароль: ");
            password = scanner.nextLine();
            System.out.print("введите Роль admin или user: ");
            String role = scanner.nextLine();
            role.toUpperCase();
            System.out.println(role);
            front.registerUser(userName, password, Role.valueOf(role));
        } while (!front.checkUser(userName, password));


        Service.saveProduct(shop.getProductMap());
        Service.saveUser(front.getUserMap());
    }
}