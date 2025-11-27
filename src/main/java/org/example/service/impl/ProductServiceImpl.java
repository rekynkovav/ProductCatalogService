package org.example.service.impl;

import org.example.model.entity.Category;
import org.example.model.entity.Product;
import org.example.repository.ProductRepository;
import org.example.repository.UserRepository;
import org.example.repository.impl.ProductRepositoryImpl;
import org.example.service.ProductService;

import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса товаров.
 * Обеспечивает бизнес-логику управления товарами магазина.
 * Реализует паттерн Singleton.
 */
public class ProductServiceImpl implements ProductService {


    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ProductServiceImpl(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    /**
     * Сохраняет новый товар в магазине.
     * Собирает метрики добавления товаров.
     *
     * @param product товар для сохранения
     * @return
     */
    @Override
    public Product saveProduct(Product product) {
        productRepository.save(product);
        return product;
    }

    /**
     * Обновляет информацию о существующем товаре.
     *
     * @throws RuntimeException если товар с указанным идентификатором не найден
     */
    @Override
    public Product updateProduct(Product product) {
        Optional<Product> productOptional = productRepository.findById(product.getId());
        if (productOptional.isPresent()) {
            Product newProduct = productOptional.get();
            newProduct.setName(product.getName());
            newProduct.setQuantity(product.getQuantity());
            newProduct.setPrice(product.getPrice());
            newProduct.setCategory(product.getCategory());
            productRepository.update(newProduct);

        } else {
            throw new RuntimeException("Product not found with id: " + product.getId());
        }
        return product;
    }

    /**
     * Удаляет товар по идентификатору.
     * Собирает метрики удаления товаров.
     *
     * @param id идентификатор товара для удаления
     */
    @Override
    public boolean deleteProductById(long id) {
        return productRepository.deleteById(id);
    }

    /**
     * Ищет товары по категории и отображает результаты.
     *
     * @param category категория для поиска
     * @return
     */
    @Override
    public List<Product> searchCategory(Category category) {
        List<Product> listProduct = productRepository.findByCategory(category);
        if (!listProduct.isEmpty()) {
            listProduct.forEach(System.out::println);
            return listProduct;
        }
        System.out.println("В данной категории нет товаров");
        return listProduct;
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
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (quantity <= product.getQuantity()) {
                userRepository.addToBasket(userId, product.getId(), quantity);
                product.subtractQuantity(quantity);
                productRepository.update(product);
                System.out.println("Товар успешно добавлен в корзину");
            } else {
                System.out.println("Недостаточно товара в наличии. Доступно: " + product.getQuantity());
            }
        } else {
            System.out.println("Товар не найден");
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
    public List<Product> getAllProduct(int page) {
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
    public List<Product> getAllProduct() {
        return getAllProduct(0); // По умолчанию показываем первую страницу
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