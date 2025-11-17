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

    private static ProductServiceImpl productServiceImpl;

    public static ProductServiceImpl getInstance(){
        if (productServiceImpl == null) {
            productServiceImpl = new ProductServiceImpl();
        }
        return productServiceImpl;
    }

    private ProductServiceImpl(){

    }

    private UserServiceImpl productService = UserServiceImpl.getInstance();
    private UserSecurityConfigImpl userSecurityService = UserSecurityConfigImpl.getInstance();
    private ProductRepositoryImpl productRepositoryImpl = ProductRepositoryImpl.getInstance();
    private AuditRepositoryImpl auditRepositoryImpl = AuditRepositoryImpl.getInstance();
    private UserRepositoryImpl userRepository = UserRepositoryImpl.getInstance();

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
        System.out.println();
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
        if (productRepositoryImpl.getProductMap().containsKey(id)) {
            int availableQuantity = productRepositoryImpl.getProductMap().get(id).getQuantity();
            if (quantity <= availableQuantity) {
                if (checkingBasketForProduct(id)) { // если товар есть в корзине
                    userSecurityService.getThisUser().getMapBasket().get(id).appendQuantity(quantity);
                    serviceBasket(id, quantity);
                    return;
                } else {
                    userSecurityService.getThisUser().getMapBasket().put(id, productRepositoryImpl.getProductMap().get(id));
                    userSecurityService.getThisUser().getMapBasket().get(id).setQuantity(quantity);
                    serviceBasket(id, quantity);
                    return;
                }
            }
            System.out.println("вы можете купить только " + availableQuantity);
        }else{
            System.out.println("Такого товара нет в наличии");
        }
    }

    private void serviceBasket(long id, int quantity) {
        productRepositoryImpl.getProductMap().get(id).subtractQuantity(quantity);
        saveStatistic(id, quantity);
        System.out.println("товары успешно добавлены в корзину");
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
}

