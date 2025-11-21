package org.example.controller;

import org.example.config.impl.UserSecurityConfigImpl;
import org.example.model.entity.Category;
import org.example.model.entity.Product;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.service.impl.MetricsServiceImpl;
import org.example.service.impl.ProductServiceImpl;
import org.example.service.impl.UserServiceImpl;

import java.util.Map;
import java.util.Scanner;

/**
 * Класс для взаимодействия с пользователем через консоль
 * Обеспечивает чтение ввода с клавиатуры через Scanner
 * Использует объекты классов Shop и Service для доступа к методам магазина и авторизации/аутентификации
 * В статическом блоке инициализирует поля и выполняет десериализацию 4 компонентов:
 * - список товаров магазина
 * - список пользователей
 * - запросы и действия пользователей (как админов, так и покупателей)
 */
public class ViewingConsole {
    private String userName;
    private String password;
    private String showMenuUser;
    private String showMenuAdmin;
    private User currentUser;
    private Scanner scanner;

    private ProductServiceImpl productService;
    private UserServiceImpl userService;
    private UserSecurityConfigImpl userSecurityConfig;
    private MetricsServiceImpl metricsService;

    /**
     * Конструктор класса ViewingConsole
     * Инициализирует сервисы и Scanner для работы с консолью
     */
    public ViewingConsole() {
        productService = ProductServiceImpl.getInstance();
        userService = UserServiceImpl.getInstance();
        userSecurityConfig = UserSecurityConfigImpl.getInstance();
        metricsService = MetricsServiceImpl.getInstance();
        scanner = new Scanner(System.in);
    }

    /**
     * Запускает основной цикл работы приложения
     * Выполняет авторизацию или регистрацию пользователя
     */
    public void start() {
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
                System.out.println("Неверный пароль. Попробуйте снова.");
                do {
                    System.out.println("Введите пароль: ");
                    password = scanner.nextLine();
                } while (!userSecurityConfig.verificationUser(userName, password));
                beginSession();
            }
        } else {
            registerUser();
        }
    }

    /**
     * Регистрирует нового пользователя в системе
     * Запрашивает пароль и роль пользователя
     */
    private void registerUser() {
        System.out.print("Введите пароль: ");
        password = scanner.nextLine();
        System.out.print("введите Роль admin или user: ");
        String role = scanner.nextLine().toUpperCase();
        try {
            userSecurityConfig.registerUser(userName, password, Role.valueOf(role));
            beginSession();
        } catch (IllegalArgumentException e) {
            System.out.println("Неверная роль. Используйте 'admin' или 'user'.");
            registerUser();
        }
    }

    /**
     * Открывает сессию для админа или покупателя
     * На основе роли пользователя перенаправляет в соответствующее меню
     */
    private void beginSession() {
        if (!userSecurityConfig.isAuthenticated()) {
            System.out.println("Ошибка: пользователь не аутентифицирован");
            start();
            return;
        }

        if (userSecurityConfig.getThisUser() != null) {
            currentUser = userSecurityConfig.getThisUser();
        }

        if (currentUser.getRole() == Role.ADMIN) {
            adminBar();
        } else {
            userBar();
        }
    }

    /**
     * Показывает меню возможностей покупателя
     */
    private void showMenuUser() {
        showMenuUser = """ 
                
                Посмотреть список товаров введите: 1
                Добавить товар в корзину введите: 2
                Фильтровать товар введите по категориям: 3
                Для просмотра корзины введите: 4
                Для выхода введите: 5
                """;
        System.out.println(showMenuUser);
    }

    /**
     * Реализует консольное меню и взаимодействие с бэкендом для пользователя
     * Основано на перечне возможностей из метода showMenuUser через switch case и рекурсию
     * При выходе сериализует данные и закрывает приложение
     * Отслеживает вход/выход пользователей и добавление ими товаров в корзину
     */
    private void userBar() {
        showMenuUser();
        int input = Integer.parseInt(scanner.nextLine());
        switch (input) {
            case 1:
                productService.showAllProduct();
                userBar();
                break;
            case 2:
                addProductToBasket();
                userBar();
                break;
            case 3:
                filterProductsByCategory();
                userBar();
                break;
            case 4:
                showUserBasket();
                userBar();
                break;
            case 5:
                exitUser();
                break;
            default:
                System.out.println("Неверный ввод. Попробуйте снова.");
                userBar();
        }
    }

    /**
     * Выполняет выход пользователя из системы
     * Увеличивает счетчик выходов и завершает работу приложения
     */
    private void exitUser() {
        User currentUser = userSecurityConfig.getThisUser();
        metricsService.incrementMetric(currentUser.getId(), "LOGOUT_COUNT");

        System.out.println("До свидания!");
        System.exit(0);
    }

    /**
     * Показывает содержимое корзины пользователя
     * Отображает список товаров, их количество и общую сумму
     */
    private void showUserBasket() {
        User currentUser = userSecurityConfig.getThisUser();
        Map<Long, Product> basketMap = userService.getUserBasket(currentUser.getId());

        if (!basketMap.isEmpty()) {
            System.out.println("Ваша корзина:");
            double total = 0;
            for (Map.Entry<Long, Product> entry : basketMap.entrySet()) {
                Product product = entry.getValue();
                double itemTotal = product.getPrice() * product.getQuantity();
                System.out.println(product.getName() + ", количество: " + product.getQuantity() +
                                   " шт, цена: " + product.getPrice() + ", сумма: " + itemTotal);
                total += itemTotal;
            }
            System.out.println("Общая сумма: " + total);
        } else {
            System.out.println("Ваша корзина пуста");
        }
    }

    /**
     * Фильтрует товары по категории
     * Запрашивает у пользователя категорию и отображает соответствующие товары
     */
    private void filterProductsByCategory() {
        System.out.println("Введите категорию товара ELECTRONICS, CLOTHING, FOOD, BOOKS, EDUCATION, SPORTS, HOME, OTHER: ");
        String categoryInput = scanner.nextLine().toUpperCase();
        try {
            Category category = Category.valueOf(categoryInput);
            productService.searchCategory(category);
        } catch (IllegalArgumentException e) {
            System.err.println("Неверная категория. Попробуйте снова.");
        }
    }

    /**
     * Добавляет товар в корзину пользователя
     * Запрашивает ID товара и количество для добавления
     */
    private void addProductToBasket() {
        try {
            System.out.println("Введите номер товара: ");
            long productId = Long.parseLong(scanner.nextLine());
            System.out.println("Введите количество: ");
            int quantity = Integer.parseInt(scanner.nextLine());

            Long userId = userSecurityConfig.getThisUser().getId();
            productService.addBasket(userId, productId, quantity);
        } catch (NumberFormatException e) {
            System.err.println("Ошибка: введите корректное число");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Показывает меню возможностей администратора
     */
    private void showMenuAdmin() {
        showMenuAdmin = """
                Добавить товар введите: 1
                Изменить товар введите: 2
                Удалить товар введите: 3
                Посмотреть список товаров введите: 4
                Фильтровать товар введите: 5
                Для просмотра Активности Юзеров введите: 6
                Для выхода Введите: 7
                """;
        System.out.println(showMenuAdmin);
    }

    /**
     * Реализует консольное меню и взаимодействие с бэкендом для администратора
     * При выходе сохраняет активность админа по добавлению, изменению и удалению товаров
     */
    private void adminBar() {
        showMenuAdmin();
        int input = Integer.parseInt(scanner.nextLine());

        switch (input) {
            case 1:
                addProduct();
                adminBar();
                break;
            case 2:
                updateProduct();
                adminBar();
            case 3:
                deleteProduct();
                adminBar();
            case 4:
                productService.showAllProduct();
                adminBar();
                break;
            case 5:
                filterProductsByCategory();
                adminBar();
                break;
            case 6:
                showUserActivity();
                adminBar();
            case 7:
                exitAdmin();
                break;
            default:
                System.out.println("Неверный ввод. Попробуйте снова.");
                adminBar();
        }
    }

    /**
     * Выполняет выход администратора из системы
     * Увеличивает счетчик выходов и завершает работу приложения
     */
    private void exitAdmin() {
        User currentUser = userSecurityConfig.getThisUser();
        metricsService.incrementMetric(currentUser.getId(), MetricsServiceImpl.LOGOUT_COUNT);

        System.out.println("До свидания!");
        System.exit(0);
    }

    /**
     * Показывает статистику активности всех пользователей на основе метрик.
     * Отображает информацию о входах, действиях с товарами и корзиной.
     */
    private void showUserActivity() {
        System.out.println(metricsService.getFormattedMetrics());
    }

    /**
     * Удаляет товар из системы
     * Запрашивает ID товара для удаления и увеличивает счетчик удалений
     */
    private void deleteProduct() {
        try {
            productService.showAllProduct();
            System.out.print("Введите ID товара который хотите удалить: ");
            long id = Long.parseLong(scanner.nextLine());

            productService.deleteProductById(id);
            User currentUser = userSecurityConfig.getThisUser();
            metricsService.incrementMetric(currentUser.getId(), MetricsServiceImpl.PRODUCT_DELETE_COUNT);
            System.out.println("Товар успешно удален");

        } catch (Exception e) {
            System.out.println("Ошибка при удалении товара: " + e.getMessage());
        }
    }

    /**
     * Обновляет информацию о товаре
     * Запрашивает новые данные товара и увеличивает счетчик модификаций
     */
    private void updateProduct() {
        try {
            productService.showAllProduct();
            System.out.print("Введите ID товара который хотите изменить: ");
            long id = Long.parseLong(scanner.nextLine());
            System.out.print("Введите новое наименование товара: ");
            String name = scanner.nextLine();
            System.out.print("Введите новое количество товара: ");
            int quantity = Integer.parseInt(scanner.nextLine());
            System.out.print("Введите новую цену товара: ");
            int price = Integer.parseInt(scanner.nextLine());
            System.out.println("ELECTRONICS, CLOTHING, FOOD, BOOKS, EDUCATION, SPORTS, HOME, OTHER: ");
            System.out.print("Введите новую категорию товара: ");
            String categoryInput = scanner.nextLine().toUpperCase();

            Category category = Category.valueOf(categoryInput);
            productService.updateProduct(id, name, quantity, price, category);

            User currentUser = userSecurityConfig.getThisUser();
            metricsService.incrementMetric(currentUser.getId(), MetricsServiceImpl.PRODUCT_UPDATE_COUNT);
            System.out.println("Товар успешно изменен");

        } catch (Exception e) {
            System.out.println("Ошибка при изменении товара: " + e.getMessage());
        }
    }

    /**
     * Добавляет новый товар в систему
     * Запрашивает данные товара и увеличивает счетчик добавлений
     */
    private void addProduct() {
        try {
            System.out.print("Введите наименование товара: ");
            String name = scanner.nextLine();
            System.out.print("Введите количество товара: ");
            int quantity = Integer.parseInt(scanner.nextLine());
            System.out.print("Введите цену товара: ");
            int price = Integer.parseInt(scanner.nextLine());
            System.out.println("ELECTRONICS, CLOTHING, FOOD, BOOKS, EDUCATION, SPORTS, HOME, OTHER: ");
            System.out.print("Введите категорию товара: ");
            String categoryInput = scanner.nextLine().toUpperCase();

            Category category = Category.valueOf(categoryInput);
            Product product = new Product(name, quantity, price, category);
            productService.saveProduct(product);

            System.out.println("Товар успешно добавлен");
        } catch (Exception e) {
            System.err.println("Ошибка при добавлении товара: " + e.getMessage());
        }
    }
}