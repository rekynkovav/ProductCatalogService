package org.example.service.impl;

import org.example.config.impl.UserSecurityConfigImpl;
import org.example.model.entity.Categories;
import org.example.model.entity.Product;
import org.example.model.entity.User;
import org.example.repository.enumPath.StoragePath;
import org.example.repository.impl.AuditRepositoryImpl;
import org.example.repository.impl.ProductRepositoryImpl;
import org.example.repository.impl.UserRepositoryImpl;
import org.example.service.ProductService;

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
public class ProductServiceImpl implements ProductService {

    private UserServiceImpl userService;
    private UserSecurityConfigImpl userSecurityService;
    private ProductRepositoryImpl productRepositoryImpl;
    private AuditRepositoryImpl auditRepositoryImpl;
    private UserRepositoryImpl userRepository;

    {
        userRepository = new UserRepositoryImpl();
        productRepositoryImpl = new ProductRepositoryImpl();
        auditRepositoryImpl = new AuditRepositoryImpl();
        userService = new UserServiceImpl();
        userSecurityService = new UserSecurityConfigImpl();
    }

    public void setProductRepositoryImpl(ProductRepositoryImpl productRepositoryImpl) {
        this.productRepositoryImpl = productRepositoryImpl;
    }

    public void setUserRepository(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    public void setAuditRepository(AuditRepositoryImpl auditRepository) {
        this.auditRepositoryImpl = auditRepository;
    }

    public void setUserSecurityService(UserSecurityConfigImpl userSecurityService) {
        this.userSecurityService = userSecurityService;
    }

    @Override
    public void addProduct(Product product) {
        productRepositoryImpl.getProductMap().put(product.getId(), product);
    }

    @Override
    public void modificationProduct(long id, String name, int quantity, int price, Categories categories) {
        productRepositoryImpl.getProductMap().get(id).setName(name);
        productRepositoryImpl.getProductMap().get(id).setQuantity(quantity);
        productRepositoryImpl.getProductMap().get(id).setPrice(price);
        productRepositoryImpl.getProductMap().get(id).setCategories(categories);
    }

    @Override
    public void deleteProduct(long id) {
        productRepositoryImpl.getProductMap().remove(id);
    }

    @Override
    public void showAllProduct() {
        if (!productRepositoryImpl.getProductMap().isEmpty()) {
            for (Map.Entry<Long, Product> product : productRepositoryImpl.getProductMap().entrySet()) {
                System.out.println(product.getKey() + " "
                        + product.getValue().getName() + " цена: "
                        + product.getValue().getPrice() + " доступно: "
                        + product.getValue().getQuantity() + " шт.");
            }
        } else {
            System.out.println("товаров нет в наличии");
        }
    }

    @Override
    public void searchCategories(Categories categories) {
        List<Product> listProduct = productRepositoryImpl.getProductMap().values().stream()
                .filter(product -> product.getCategories().equals(categories)).toList();
        if (!listProduct.isEmpty()) {
            listProduct.forEach(System.out::println);
            return;
        }
        System.out.println("В данной категории нет товаров");
    }

    @Override
    public void saveAllMapFromDB() {
        productRepositoryImpl.saveProduct(productRepositoryImpl.getProductMap(), StoragePath.PRODUCT);
        productRepositoryImpl.saveProduct(auditRepositoryImpl.getMapPopularProducts(), StoragePath.POPULAR_PRODUCT);
        userRepository.saveUser(userRepository.getUserMap(), StoragePath.USER);
        userRepository.saveUser(auditRepositoryImpl.getMapRequestUser(), StoragePath.REQUEST);
    }

    @Override
    public void loadAllMapFromDB() {
        // Загружаем основные продукты
        HashMap<Long, Product> products = productRepositoryImpl.loadMapProduct(StoragePath.PRODUCT);
        if (products != null) {
            productRepositoryImpl.setProductMap(products);
        }

        // Загружаем популярные продукты
        HashMap<Long, Product> popularProducts = productRepositoryImpl.loadMapProduct(StoragePath.POPULAR_PRODUCT);
        if (popularProducts != null) {
            auditRepositoryImpl.setMapPopularProducts(popularProducts);
        }

        // Загружаем пользователей
        HashMap<String, User> users = userRepository.loadMapUser(StoragePath.USER);
        if (users != null) {
            userRepository.setUserMap(users);
        }

        // Загружаем запросы пользователей
        HashMap<String, User> userRequests = userRepository.loadMapUser(StoragePath.REQUEST);
        if (userRequests != null) {
            auditRepositoryImpl.setMapRequestUser(userRequests);
        }

    }

    @Override
    public void addBasket(long id, int quantity) {
        /**
         * метод на вход принимает id товара и количество которое хотят купить
         * делает проверку есть ли уже такой товар в корзине
         * и если есть то просто увеличиваю его количество, а в магазине уменьшаю
         * availableQuantity - доступное количество товара в магазине
         * добаляет в аудит user который совершил покупку
         * добавляет в аудит товар который добавили
         */
        int availableQuantity = productRepositoryImpl.getProductMap().get(id).getQuantity();
        if (quantity <= availableQuantity) {
            if (checkingBasketForProduct(id)) {
                userSecurityService.getThisUser().getMapBasket().put(id, productRepositoryImpl.getProductMap().get(id));
                userSecurityService.getThisUser().getMapBasket().get(id).setQuantity(quantity);
                saveStatistic(id, quantity);

                System.out.println("товары успешно добавлены в корзину");
                return;
            } else {
                userSecurityService.getThisUser().getMapBasket().put(id, productRepositoryImpl.getProductMap().get(id));
                userSecurityService.getThisUser().getMapBasket().get(id).setQuantity(quantity);
                saveStatistic(id, quantity);
                System.out.println("товары успешно добавлены в корзину");
                return;
            }
        }
        System.out.println("вы можете купить только " + availableQuantity);
    }

    @Override
    public boolean checkingBasketForProduct(long id) {
        return userSecurityService.getThisUser().getMapBasket().containsKey(id);
    }

    private void saveStatistic(long id, int quantity) {
        productRepositoryImpl.getProductMap().get(id).subtractQuantity(quantity);
        auditRepositoryImpl.auditUserAddBasket(userSecurityService.getThisUser().getUserName());
        auditRepositoryImpl.setPopularProducts(id);
    }

    public void setProductRepository(ProductRepositoryImpl productRepository) {
        this.productRepositoryImpl = productRepository;
    }

    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }
}

