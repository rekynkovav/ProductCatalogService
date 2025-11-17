package org.example.controller;

import org.example.config.impl.UserSecurityConfigImpl;
import org.example.model.entity.Categories;
import org.example.model.entity.Product;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.service.impl.AuditServiceImpl;
import org.example.service.impl.ProductServiceImpl;
import org.example.service.impl.UserServiceImpl;

import java.util.Map;
import java.util.Scanner;

/**
 * Класс для взаимодействия с пользователем через консоль
 * для чтения ввода с клавиатуры Scanner
 * 2 объекта классов Shop и Service для доступа к методам магазина и авторизации аутентификации
 * статическом блоке инициализации полей
 * десириализацию 4 компонентов которые:
 * хранят список товаров магазина
 * пользователей
 * запросы и действия пользователей как админов так и покупателей
 */
public class ViewingConsole {

    private String userName;
    private String password;
    private Scanner scanner;
    private String showMenuUser;
    private String showMenuAdmin;

    private ProductServiceImpl productService = ProductServiceImpl.getInstance();
    private UserServiceImpl userService = UserServiceImpl.getInstance();
    private UserSecurityConfigImpl userSecurityConfig = UserSecurityConfigImpl.getInstance();
    private AuditServiceImpl auditService = AuditServiceImpl.getInstance();

    {
        showMenuUser = """
                """;
        showMenuAdmin = """
                """;
        scanner = new Scanner(System.in);
    }

    /**
     * метод Start для авторизации или регистрации новых пользователей
     * и открывает сессию с покупателем или админом
     */
    public void start() {
        productService.loadAllMapFromDB();

        System.out.print("""
                Добро пожаловать на маркетплейс
                Для просмотра и покупки товаров, Авторизуйтесь или Зарегистрируйтесь
                Введите логин: """);
        userName = scanner.nextLine();
        if (userService.isContainsUser(userName)) {
            System.out.print("Введите пароль: ");
            password = scanner.nextLine();
            if (userSecurityConfig.verificationUser(userName, password)) {
                beginSession();
            } else {
                System.out.println("Введите пароль: ");
                do {
                    password = scanner.nextLine();
                } while (!userSecurityConfig.verificationUser(userName, password));
                beginSession();
            }
        }
        System.out.print("Введите пароль: ");
        password = scanner.nextLine();
        System.out.print("введите Роль admin или user: ");
        String role = scanner.nextLine();
        try {
            userSecurityConfig.registerUser(userName, password, Role.valueOf(role));
            beginSession();
        } catch (IllegalArgumentException e) {
            System.out.println("Неверная роль. Используйте 'admin' или 'user'.");
            start();
        }
    }

    /**
     * метод beginSession открывает сессию для админа или покупателя
     */
    private void beginSession() {
        if (!userSecurityConfig.isAuthenticated()) {
            System.out.println("Ошибка: пользователь не аутентифицирован");
            start();
            return;
        }
        if (userSecurityConfig.getThisUser().getRole() == Role.admin) {
            adminBar();
        } else {
            userBar();
        }
    }

    /**
     * метод showMenuUser перечисляет возможности покупателя
     */
    private void showMenuUser() {
        showMenuUser = """ 
                
                Посмотреть список товаров введите: 1
                Добавить товар в корзину введите: 2
                Фильтровать товар введите по категориям: 3
                Для просмотра корзины введите: 4
                Для просмотра часто запрашиваемых товаров введите: 5
                Для выхода введите: 6
                """;
        System.out.println(showMenuUser);
    }

    /**
     * метод userBar основываясь на перечне возможностей покупателя из метода showMenuUser через switch case и рекурсии
     * реализовывает консольное меню и взаимодействие с бэкендом.
     * При выходе сериализует данные закрывает приложение
     * отслеживает вход/выход пользователей добавление ими товаров в корзину
     */
    private void userBar() {
        long id;
        String name;
        int quantity;
        int price;
        Categories categories;
        showMenuUser();
        switch (scanner.nextInt()) {
            case 1:
                productService.showAllProduct();
                userBar();
            case 2:
                System.out.println("Введите номер товара: ");
                id = scanner.nextLong();
                System.out.println("Введите количество: ");
                quantity = scanner.nextInt();
                productService.addBasket(id, quantity);
                userBar();
            case 3:
                System.out.println("Введите категорию товара clothes, food, tools, electronics, other: ");
                categories = Categories.valueOf(scanner.next());
                productService.searchCategories(categories);
                userBar();
            case 4:
                if (!userSecurityConfig.getThisUser().getMapBasket().isEmpty()) {
                    for (Map.Entry<Long, Product> entry : userSecurityConfig.getThisUser().getMapBasket().entrySet()) {
                        System.out.println(entry.getValue().getName() + ", " + "в количестве: " + entry.getValue().getQuantity() + " шт," + " сумма: " + entry.getValue().getPrice() * entry.getValue().getQuantity());
                    }
                } else {
                    System.out.println("Ваша корзина пуста");
                }
                userBar();
            case 5:
                printMap();
                userBar();
            case 6:
                userSecurityConfig.getThisUser().appendOut();
                productService.saveAllMapFromDB();
                System.exit(0);
        }
    }

    private void printMap() {
        if (!auditService.getPopularProductsMap().isEmpty()) {
            for (Map.Entry<Long, Product> entry : auditService.getPopularProductsMap().entrySet()) {
                System.out.println(entry.getValue().getName() + ", " + "запрашивали: " + entry.getValue().getQuantity() + " раз");
            }
        } else {
            System.out.println("Список часто запрашиваемых товаров пуст");
        }
    }

    /**
     * аналогичных 2 метода для админа
     */
    private void showMenuAdmin() {
        showMenuAdmin = """
                Добавить товар введите: 1
                Изменить товар введите: 2
                Удалить товар введите: 3
                Посмотреть список товаров введите: 4
                Фильтровать товар введите: 5
                Для просмотра часто запрашиваемых товаров введите: 6
                Для просмотра Активности Юзеров введите: 7
                Для выхода Введите: 8
                """;
        System.out.println(showMenuAdmin);
    }

    /**
     * при выходе сохраняет активность админа по добавлению изменению удалению товаров
     */
    private void adminBar() {
        long id;
        String name;
        int quantity;
        int price;
        Categories categories;
        System.out.println();
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
                productService.addProduct(new Product(name, quantity, price, categories));
                System.out.println("Товар успешно добавлен");
                userSecurityConfig.getThisUser().AddCountProducts();
                adminBar();
            case 2:
                productService.showAllProduct();
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
                productService.modificationProduct(id, name, quantity, price, categories);
                userSecurityConfig.getThisUser().addModificationProducts();
                productService.showAllProduct();
                adminBar();
            case 3:
                productService.showAllProduct();
                System.out.println("Введите номер товара который хотите удалить: ");
                id = scanner.nextLong();
                productService.deleteProduct(id);
                userSecurityConfig.getThisUser().addCountDeleteProducts();
                productService.showAllProduct();
                adminBar();
            case 4:
                productService.showAllProduct();
                adminBar();
            case 5:
                System.out.println("Введите категорию товара clothes, food, tools, electronics, other: ");
                categories = Categories.valueOf(scanner.next());
                productService.searchCategories(categories);
                adminBar();
            case 6:
                printMap();
                adminBar();
            case 7:
                if (!auditService.getRequestUserMap().isEmpty()) {
                    for (Map.Entry<String, User> entry : auditService.getRequestUserMap().entrySet()) {
                        System.out.println("Пользователь " + entry.getValue().getUserName()
                                + " роль " + entry.getValue().getRole()
                                + " вошел/вышел " + entry.getValue().getIn() + " раз" + " его активность: " + " добавил товаров в магазин" + entry.getValue().getAddProducts()
                                + ", изменил товаров " + entry.getValue().getModificationProducts() + ", удалил товаров из магазина" + entry.getValue().getDeleteProducts()
                                + ", добавил товаров в корзину " + entry.getValue().getAddBasket());
                    }
                } else {
                    System.out.println("Список активности пользователей пуст");
                }
                adminBar();
            case 8:
                userSecurityConfig.getThisUser().appendOut();
                productService.saveAllMapFromDB();
                System.exit(0);
        }
    }
}

