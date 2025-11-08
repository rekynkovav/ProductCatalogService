package org.example;

import org.example.entity.Categories;
import org.example.entity.Product;
import org.example.entity.Role;
import org.example.front.Front;
import org.example.service.Service;
import org.example.shop.Shop;

import java.util.Scanner;

public class Main {
    private static Shop shop;
    private static Front front;
    private static String userName;
    private static String password;
    private static Scanner scanner;

    public static void main(String[] args) {

        shop = new Shop();
        front = new Front();
        shop.setProductMap(Service.loadProduct());
        front.setUserMap(Service.loadUser());
        scanner = new Scanner(System.in);

//        shop.addProduct(new Product("хлеб", 70, 65, Categories.food));
//        shop.addProduct(new Product("масло", 5, 317, Categories.food));
//        shop.addProduct(new Product("NOKIA 1100", 7, 1200, Categories.electronics));
//        shop.addProduct(new Product("плоскогубцы", 12, 362, Categories.tools));
//        shop.addProduct(new Product("куртка", 2, 6500, Categories.clothes));
//        shop.addProduct(new Product("удочка", 6, 650, Categories.other));
        System.out.println("Добро пожаловать на маркетплейс");
        System.out.println("Для просмотра и покупки товаров, Авторизуйтесь или Зарегистрируйтесь");
        System.out.print("Введите логин: ");
        userName = scanner.nextLine();
        if (front.getUserMap().containsKey(userName)) {
            System.out.print("Введите пароль: ");
            password = scanner.nextLine();
            if (front.checkUser(userName, password)) {
                startApplication();
            } else {
                System.out.println("Введите пароль: ");
                do {
                    password = scanner.nextLine();
                } while (!front.checkUser(userName, password));
                startApplication();
            }
        }
        System.out.print("Введите пароль: ");
        password = scanner.nextLine();
        System.out.print("введите Роль admin или user: ");
        String role = scanner.nextLine();
        front.registerUser(userName, password, Role.valueOf(role));

        startApplication();
    }

    private static void startApplication() {

        System.out.println();
        shop.showAllProduct();
        System.out.println();
        if (front.getUser(userName).getRole() == Role.admin) {
            adminBar();
        } else {
            userBar();
        }

        Service.saveProduct(shop.getProductMap());
        Service.saveUser(front.getUserMap());
        System.exit(0);
    }

    private static void userBar() {
    }

    private static void adminBar() {
        long id;
        String name;
        int quantity;
        int price;
        Categories categories;
        showMenuAdmin();
        switch (scanner.nextInt()) {
            case 1:
                System.out.println("Введите наименование товара: ");
                name = scanner.next();
                System.out.println("Введите количество товара: ");
                quantity = scanner.nextInt();
                System.out.println("Введите цену товара: ");
                price = scanner.nextInt();
                System.out.println("Введите категорию товара clothes, food, tools, electronics, other: ");
                categories = Categories.valueOf(scanner.next());
                shop.addProduct(new Product(name, quantity, price, categories));
                break;
            case 2:
                shop.showAllProduct();
                System.out.println("Введите номер товара который хотите изменить: ");
                id = scanner.nextLong();
                System.out.println("Введите наименование товара: ");
                name = scanner.next();
                System.out.println("Введите количество товара: ");
                quantity = scanner.nextInt();
                System.out.println("Введите цену товара: ");
                price = scanner.nextInt();
                System.out.println("Введите категорию товара clothes, food, tools, electronics, other: ");
                categories = Categories.valueOf(scanner.next());
                shop.changeProduct(id, name, quantity, price, categories);
                shop.showAllProduct();
                break;
            case 3:
                shop.showAllProduct();
                System.out.println("Введите номер товара который хотите удалить: ");
                id = scanner.nextLong();
                shop.deleteProduct(id);
                shop.showAllProduct();
                break;
            case 4:
                shop.showAllProduct();
                adminBar();
                break;
            case 5:
                shop.showAllProduct();
                System.out.println("Введите категорию товара clothes, food, tools, electronics, other: ");
                categories = Categories.valueOf(scanner.next());
                shop.searchCategories(categories);
                break;
            case 6:
                System.exit(0);
        }
    }

    private static void showMenuAdmin() {
        System.out.println("Добавить товар введите: 1");
        System.out.println("Изменить товар введите: 2");
        System.out.println("Удалить товар введите: 3");
        System.out.println("Посмотреть список товаров введите: 4");
        System.out.println("Фильтровать товар введите: 5 и через пробел категорию clothes, food, tools, electronics, other");
        System.out.println("Введите 6 для выхода");

    }
}