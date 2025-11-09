package org.example.console;

import org.example.entity.Categories;
import org.example.entity.Product;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.repository.Repository;
import org.example.service.BusinessInfo;
import org.example.service.Service;
import org.example.shop.Shop;

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
    private static Shop shop;
    private static Service service;
    private static String userName;
    private static String password;
    private static Scanner scanner;

    static {
        shop = new Shop();
        service = new Service();
        shop.setProductMap(Repository.loadProduct());
        service.setUserMap(Repository.loadUser());
        BusinessInfo.setRequestProducts(Repository.loadRequests());
        BusinessInfo.setRequestUser(Repository.loadBusinessInfoUser());
        scanner = new Scanner(System.in);
    }
    /**
     * метод Start для авторизации или регистрации новых пользователей
     * и открывает сессию с покупателем или админом
     */
    public static void start() {

        System.out.println("Добро пожаловать на маркетплейс");
        System.out.println("Для просмотра и покупки товаров, Авторизуйтесь или Зарегистрируйтесь");
        System.out.print("Введите логин: ");
        userName = scanner.nextLine();
        if (service.getUserMap().containsKey(userName)) {
            System.out.print("Введите пароль: ");
            password = scanner.nextLine();
            if (service.checkUser(userName, password)) {
                beginSession();
            } else {
                System.out.println("Введите пароль: ");
                do {
                    password = scanner.nextLine();
                } while (!service.checkUser(userName, password));
                beginSession();
            }
        }
        System.out.print("Введите пароль: ");
        password = scanner.nextLine();
        System.out.print("введите Роль admin или user: ");
        String role = scanner.nextLine();
        service.registerUser(userName, password, Role.valueOf(role));
        beginSession();
    }

    /**
     * метод beginSession открывает сессию для админа или покупателя
     */
    private static void beginSession() {
        System.out.println();
        shop.showAllProduct();
        System.out.println();
        if (Service.getThisUser().getRole() == Role.admin) {
            adminBar();
        } else {
            userBar();
        }
    }
    /**
     * метод showMenuUser перечисляет возможности покупателя
     */
    private static void showMenuUser() {
        System.out.println("Посмотреть список товаров введите: 1");
        System.out.println("Добавить товар в корзину введите: 2");
        System.out.println("Фильтровать товар введите: 3");
        System.out.println("Для просмотра корзины введите: 4");
        System.out.println("Для просмотра часто запрашиваемых товаров введите: 5");
        System.out.println("Для выхода введите: 6");
    }
    /**
     * метод userBar основываясь на перечне возможностей покупателя из метода showMenuUser через switch case и рекурсии
     * реализовывает консольное меню и взаимодействие с бэкендом.
     * При выходе сериализует данные закрывает приложение
     * отслеживает вход/выход пользователей добавление ими товаров в корзину
     */
    private static void userBar() {
        long id;
        String name;
        int quantity;
        int price;
        Categories categories;
        System.out.println();
        showMenuUser();
        switch (scanner.nextInt()) {
            case 1:
                shop.showAllProduct();
                userBar();
            case 2:
                System.out.println("Введите номер товара: ");
                id = scanner.nextLong();
                System.out.println("Введите количество: ");
                quantity = scanner.nextInt();
                System.out.println(shop.addBasket(id, quantity));
                Service.getThisUser().setAddBasket(Service.getThisUser().getAddBasket() + 1);
                BusinessInfo.getRequestProductsMap().get(id).setQuantity(BusinessInfo.getRequestProductsMap().get(id).getQuantity() + 1);
                shop.showAllProduct();
                userBar();
            case 3:
                System.out.println("Введите категорию товара clothes, food, tools, electronics, other: ");
                categories = Categories.valueOf(scanner.next());
                shop.searchCategories(categories);
                userBar();
            case 4:
                if (Service.getThisUser().getMapBasket().size() != 0) {
                    for (Map.Entry<Long, Product> entry : Service.getThisUser().getMapBasket().entrySet()) {
                        System.out.println(entry.getValue().getName() + ", " + "в количестве: " + entry.getValue().getQuantity() + " шт," + " сумма: " + entry.getValue().getPrice() * entry.getValue().getQuantity());
                    }
                } else {
                    System.out.println("Ваша корзина пуста");
                }
                userBar();
            case 5:
                if (!BusinessInfo.getRequestProductsMap().isEmpty()) {
                    for (Map.Entry<Long, Product> entry : BusinessInfo.getRequestProductsMap().entrySet()) {
                        System.out.println(entry.getValue().getName() + ", " + "запрашивали: " + entry.getValue().getQuantity() + " раз");
                    }
                } else {
                    System.out.println("Список часто запрашиваемых товаров пуст");
                }
                userBar();
            case 6:
                Service.getThisUser().setOut(Service.getThisUser().getOut() + 1);
                BusinessInfo.getRequestUserMap().put(Service.getThisUser().getUserName(), Service.getThisUser());
                Repository.saveProduct(shop.getProductMap());
                Repository.saveUser(service.getUserMap());
                Repository.saveRequests(BusinessInfo.getRequestProductsMap());
                Repository.saveBusinessInfoUser(BusinessInfo.getRequestUserMap());
                System.exit(0);
        }
    }
    /**
     * аналогичных 2 метода для админа
     */
    private static void showMenuAdmin() {
        System.out.println("Добавить товар введите: 1");
        System.out.println("Изменить товар введите: 2");
        System.out.println("Удалить товар введите: 3");
        System.out.println("Посмотреть список товаров введите: 4");
        System.out.println("Фильтровать товар введите: 5");
        System.out.println("Для просмотра часто запрашиваемых товаров введите: 6");
        System.out.println("Для просмотра Активности Юзеров введите: 7");
        System.out.println("Для выхода Введите: 8");
    }
    /**
     * при выходе сохраняет активность админа по добавлению изменению удалению товаров
     */
    private static void adminBar() {
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
                shop.addProduct(new Product(name, quantity, price, categories));
                Service.getThisUser().setAddProducts(Service.getThisUser().getAddProducts() + 1);
                adminBar();
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
                Service.getThisUser().setModificationProducts(Service.getThisUser().getModificationProducts() + 1);
                shop.showAllProduct();
                adminBar();
            case 3:
                shop.showAllProduct();
                System.out.println("Введите номер товара который хотите удалить: ");
                id = scanner.nextLong();
                shop.deleteProduct(id);
                Service.getThisUser().setDeleteProducts(Service.getThisUser().getDeleteProducts() + 1);
                shop.showAllProduct();
                adminBar();
            case 4:
                shop.showAllProduct();
                adminBar();
            case 5:
                System.out.println("Введите категорию товара clothes, food, tools, electronics, other: ");
                categories = Categories.valueOf(scanner.next());
                shop.searchCategories(categories);
                adminBar();
            case 6:
                if (!BusinessInfo.getRequestProductsMap().isEmpty()) {
                    for (Map.Entry<Long, Product> entry : BusinessInfo.getRequestProductsMap().entrySet()) {
                        System.out.println(entry.getValue().getName() + ", " + "запрашивали: " + entry.getValue().getQuantity() + " раз");
                    }
                } else {
                    System.out.println("Список часто запрашиваемых товаров пуст");
                }
                adminBar();
            case 7:
                if (!BusinessInfo.getRequestUserMap().isEmpty()) {
                    for (Map.Entry<String, User> entry : BusinessInfo.getRequestUserMap().entrySet()) {
                        System.out.println("Пользователь " + entry.getValue().getUserName()
                                           + " роль " + entry.getValue().getRole()
                                           + " вошел/вышел " + entry.getValue().getIn() + " раз" + " его активность: " + " добавил товаров в магазин" + entry.getValue().getAddProducts()
                                           + ", изменил товаров " + entry.getValue().getModificationProducts() + ", удалил товаров из магазина" + entry.getValue().getDeleteProducts()
                                           + ", добавил товаров в корзину " + entry.getValue().getAddBasket());
                    }
                } else {
                    System.out.println("Список активности юзеров пуст");
                }
                adminBar();
            case 8:
                Service.getThisUser().setOut(Service.getThisUser().getOut() + 1);
                BusinessInfo.getRequestUserMap().put(Service.getThisUser().getUserName(), Service.getThisUser());
                Repository.saveProduct(shop.getProductMap());
                Repository.saveUser(service.getUserMap());
                Repository.saveRequests(BusinessInfo.getRequestProductsMap());
                Repository.saveBusinessInfoUser(BusinessInfo.getRequestUserMap());
                System.exit(0);
        }
    }
}

