package org.example.service.impl;

import org.example.config.MetricsConfig;
import org.example.context.ApplicationContext;
import org.example.model.entity.Category;
import org.example.model.entity.Product;
import org.example.repository.ProductRepository;
import org.example.repository.UserRepository;
import org.example.repository.impl.ProductRepositoryImpl;
import org.example.repository.impl.UserRepositoryImpl;
import org.example.service.MetricsService;
import org.example.service.ProductService;
import org.example.service.SecurityService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Реализация сервиса товаров.
 * Обеспечивает бизнес-логику управления товарами магазина.
 * Собирает метрики операций с товарами и корзиной.
 * Реализует паттерн Singleton.
 */
public class ProductServiceImpl implements ProductService {


    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final MetricsConfig metricsConfig;
    private final ProductRepository productRepository;
    private final MetricsService metricsService;

    public ProductServiceImpl(SecurityService securityService, UserRepository userRepository, MetricsConfig metricsConfig, ProductRepository productRepository, MetricsService metricsService) {
        this.securityService = securityService;
        this.userRepository = userRepository;
        this.metricsConfig = metricsConfig;
        this.productRepository = productRepository;
        this.metricsService = metricsService;
    }

    /**
     * Сохраняет новый товар в магазине.
     * Собирает метрики добавления товаров.
     *
     * @param product товар для сохранения
     */
    @Override
    public void saveProduct(Product product) {
        long startTime = System.currentTimeMillis();
        try {
            productRepository.save(product);

            // Сохраняем метрики в БД
            if (securityService.isAuthenticated()) {
                Long userId = securityService.getThisUser().getId();
                metricsService.incrementMetric(userId, "PRODUCT_ADD_COUNT");
            }

            // Micrometer метрики
            metricsConfig.getProductAddCounter().increment();
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            metricsConfig.getProductOperationTimer().record(duration, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Обновляет информацию о существующем товаре.
     * Собирает метрики обновления товаров.
     *
     * @param id       идентификатор товара
     * @param name     новое название товара
     * @param quantity новое количество товара
     * @param price    новая цена товара
     * @param category новая категория товара
     * @throws RuntimeException если товар с указанным идентификатором не найден
     */
    @Override
    public void updateProduct(long id, String name, int quantity, int price, Category category) {
        long startTime = System.currentTimeMillis();
        try {
            Optional<Product> productOptional = productRepository.findById(id);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                product.setName(name);
                product.setQuantity(quantity);
                product.setPrice(price);
                product.setCategory(category);
                productRepository.update(product);

                // Micrometer метрики
                metricsConfig.getProductUpdateCounter().increment();
            } else {
                throw new RuntimeException("Product not found with id: " + id);
            }
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            metricsConfig.getProductOperationTimer().record(duration, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Удаляет товар по идентификатору.
     * Собирает метрики удаления товаров.
     *
     * @param id идентификатор товара для удаления
     */
    @Override
    public void deleteProductById(long id) {
        long startTime = System.currentTimeMillis();
        try {
            productRepository.deleteById(id);

            // Micrometer метрики
            metricsConfig.getProductDeleteCounter().increment();

        } finally {
            long duration = System.currentTimeMillis() - startTime;
            metricsConfig.getProductOperationTimer().record(duration, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Ищет товары по категории и отображает результаты.
     *
     * @param category категория для поиска
     */
    @Override
    public void searchCategory(Category category) {
        List<Product> listProduct = productRepository.findByCategory(category);
        if (!listProduct.isEmpty()) {
            listProduct.forEach(System.out::println);
            return;
        }
        System.out.println("В данной категории нет товаров");
    }

    /**
     * Добавляет товар в корзину пользователя.
     * Проверяет наличие достаточного количества товара.
     * Собирает метрики добавления в корзину.
     *
     * @param userId    идентификатор пользователя
     * @param productId идентификатор товара
     * @param quantity  количество товара для добавления
     */
    @Override
    public void addBasket(long userId, long productId, int quantity) {
        long startTime = System.currentTimeMillis();
        try {
            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                if (quantity <= product.getQuantity()) {
                    userRepository.addToBasket(userId, product.getId(), quantity);
                    product.subtractQuantity(quantity);
                    productRepository.update(product);

                    // Сохраняем метрики в БД
                    metricsService.incrementMetric(userId, "BASKET_ADD_COUNT");

                    System.out.println("Товар успешно добавлен в корзину");
                } else {
                    System.out.println("Недостаточно товара в наличии. Доступно: " + product.getQuantity());
                }
            } else {
                System.out.println("Товар не найден");
            }
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            metricsConfig.getProductOperationTimer().record(duration, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Находит товар по идентификатору.
     *
     * @param id идентификатор товара
     * @return Optional с найденным товаром или empty если товар не найден
     */
    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public void removeBasket(Long userId, Long productId) {
        productRepository.removeBasket(userId, productId);
    }

    @Override
    public List<Product> findByName(String nameProduct) {
        return productRepository.findByName(nameProduct);
    }

    @Override
    public List<Product> showAllProduct(int page) {
        List<Product> productList = productRepository.findAll(page);
        displayProducts(productList, page);
        return productList;
    }

    @Override
    public int getTotalPages() {
        int totalProducts = productRepository.getTotalProductsCount();
        return (int) Math.ceil((double) totalProducts / ProductRepositoryImpl.PAGE_SIZE);
    }
    /**
     * Отображает все товары в магазине с пагинацией.
     * Выводит список в консоль.
     */
    @Override
    public List<Product> showAllProduct() {
        return showAllProduct(0); // По умолчанию показываем первую страницу
    }

    /**
     * Вспомогательный метод для отображения товаров с информацией о пагинации
     */
    private void displayProducts(List<Product> productList, int currentPage) {
        if (!productList.isEmpty()) {
            System.out.println("=== Страница " + (currentPage + 1) + " ===" + "\n");
            for (Product product : productList) {
                System.out.println(product.getId() + " "
                                   + product.getName() + " цена: "
                                   + product.getPrice() + " доступно: "
                                   + product.getQuantity() + " шт. Категория: "
                                   + product.getCategory());
            }

            // Показываем информацию о пагинации
            int totalPages = getTotalPages();
            System.out.println("\n--- Страница " + (currentPage + 1) + " из " + totalPages + " ---");
            if (currentPage > 0) {
                System.out.print("Для предыдущей страницы введите: 'prev'");
            }
            if (currentPage < totalPages - 1) {
                if (currentPage > 0) System.out.print(" | ");
                System.out.print("Для следующей страницы введите: 'next'");
            }
            System.out.println();
        } else {
            System.out.println("товаров нет в наличии");
        }
        System.out.println();
    }
}