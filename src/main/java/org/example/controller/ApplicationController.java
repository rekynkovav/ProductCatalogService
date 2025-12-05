package org.example.controller;

import javax.validation.Valid;
import org.example.dto.BasketDTO;
import org.example.dto.CategoryDTO;
import org.example.dto.ProductDTO;
import org.example.dto.UserDTO;
import org.example.mapper.CategoryMapper;
import org.example.mapper.ProductMapper;
import org.example.mapper.UserMapper;
import org.example.model.entity.Category;
import org.example.model.entity.Product;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.service.CategoryService;
import org.example.service.ProductService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST контроллер для управления товарами и категориями.
 * с разделением прав доступа для пользователей и администраторов.
 *
 * @see Product
 * @see Category
 * @see ProductService
 * @see CategoryService
 * @since 2.0
 */
@RestController
@RequestMapping("/api")
public class ApplicationController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    @Autowired
    public ApplicationController(ProductService productService, CategoryService categoryService, UserService userService, ProductMapper productMapper, CategoryMapper categoryMapper, UserMapper userMapper) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.userMapper = userMapper;
    }

    private final Map<String, User> activeSessions = new HashMap<>();

    /**
     * Регистрация нового пользователя.
     */
    @PostMapping("/auth/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody UserDTO userDTO) {
        if (userService.isContainsUser(userDTO.getUserName())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Пользователь с таким именем уже существует"));
        }

        User user = userMapper.createUserFromDTO(userDTO);
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        userService.saveUser(user);

        String token = generateToken(user.getUserName(), user.getPassword());
        activeSessions.put(token, user);

        UserDTO.AuthResponse authResponse = new UserDTO.AuthResponse();
        authResponse.setMessage("Пользователь успешно зарегистрирован");
        authResponse.setToken(token);
        authResponse.setUser(userMapper.toUserInfo(user));

        return ResponseEntity.ok(Map.of(
                "message", authResponse.getMessage(),
                "token", authResponse.getToken(),
                "user", authResponse.getUser()
        ));
    }

    /**
     * Вход в систему.
     */
    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody UserDTO.LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isEmpty() || !userOptional.get().getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Неверное имя пользователя или пароль"));
        }

        User user = userOptional.get();
        String token = generateToken(username, password);
        activeSessions.put(token, user);

        UserDTO.AuthResponse authResponse = new UserDTO.AuthResponse();
        authResponse.setMessage("Успешный вход в систему");
        authResponse.setToken(token);
        authResponse.setUser(userMapper.toUserInfo(user));

        return ResponseEntity.ok(Map.of(
                "message", authResponse.getMessage(),
                "token", authResponse.getToken(),
                "user", authResponse.getUser()
        ));
    }

    /**
     * Выход из системы.
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String token) {
        String cleanToken = extractToken(token);
        if (activeSessions.remove(cleanToken) != null) {
            return ResponseEntity.ok(Map.of("message", "Успешный выход из системы"));
        }
        return ResponseEntity.ok(Map.of("message", "Сессия не найдена"));
    }

    /**
     * Получение списка всех категорий.
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        return ResponseEntity.ok(categoryMapper.toDTOList(categories));
    }

    /**
     * Получение категории по ID.
     */
    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(categoryMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получение товаров по категории.
     */
    @GetMapping("/categories/{id}/products")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long id) {
        List<Product> products = categoryService.findProductsByCategoryId(id);
        return ResponseEntity.ok(productMapper.toDTOList(products));
    }

    /**
     * Получение списка всех товаров с пагинацией.
     */
    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (page < 0){
            page = 0;
        }
        if (size <= 0 || size > 100) {
            size = 20;
        }

        List<Product> products = productService.findAllPaginated(page, size);
        long totalProducts = productService.count();
        long totalPages = (long) Math.ceil((double) totalProducts / size);

        Map<String, Object> response = new HashMap<>();
        response.put("products", productMapper.toDTOList(products));
        response.put("page", page);
        response.put("size", size);
        response.put("totalProducts", totalProducts);
        response.put("totalPages", totalPages);
        response.put("hasNext", page < totalPages - 1);
        response.put("hasPrevious", page > 0);

        return ResponseEntity.ok(response);
    }

    /**
     * Получение товара по ID.
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return productService.findById(id)
                .map(productMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получение товаров по категории (альтернативный путь).
     */
    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategoryId(@PathVariable Long categoryId) {
        List<Product> products = productService.findByCategoryId(categoryId);
        return ResponseEntity.ok(productMapper.toDTOList(products));
    }

    /**
     * Проверка существования пользователя.
     */
    @GetMapping("/auth/users/exists/{username}")
    public ResponseEntity<Map<String, Boolean>> userExists(@PathVariable String username) {
        boolean exists = userService.isContainsUser(username);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * Получение профиля текущего пользователя.
     */
    @GetMapping("/user/profile")
    public ResponseEntity<?> getCurrentUserProfile(@RequestHeader("Authorization") String token) {
        Optional<User> userOptional = getUserFromToken(token);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Неавторизованный доступ"));
        }

        User user = userOptional.get();
        return ResponseEntity.ok(userMapper.toUserInfo(user));
    }

    /**
     * Получение корзины текущего пользователя.
     */
    @GetMapping("/user/basket")
    public ResponseEntity<?> getUserBasket(@RequestHeader("Authorization") String token) {
        Optional<User> userOptional = getUserFromToken(token);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Неавторизованный доступ"));
        }

        User user = userOptional.get();
        Map<Long, Product> basket = userService.getUserBasket(user.getId());

        BasketDTO basketDTO = new BasketDTO();
        Map<Long, BasketDTO.ProductItem> basketItems = new HashMap<>();

        basket.forEach((productId, product) -> {
            BasketDTO.ProductItem item = new BasketDTO.ProductItem();
            item.setId(product.getId());
            item.setName(product.getName());
            item.setQuantity(product.getQuantity());
            item.setPrice(product.getPrice());
            item.setCategoryId(product.getCategoryId());
            basketItems.put(productId, item);
        });

        basketDTO.setItems(basketItems);
        return ResponseEntity.ok(basketDTO);
    }

    /**
     * Добавление товара в корзину пользователя.
     */
    @PostMapping("/user/basket/add/{productId}")
    public ResponseEntity<?> addToBasket(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId,
            @Valid @RequestBody BasketDTO.AddToBasketRequest request) {

        Optional<User> userOptional = getUserFromToken(token);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Неавторизованный доступ"));
        }

        User user = userOptional.get();
        int quantity = request.getQuantity();

        if (quantity <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Количество должно быть положительным"));
        }

        Optional<Product> product = productService.findById(productId);

        if (product.isEmpty() || product.get().getQuantity() < quantity) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Недостаточно товара на складе"));
        }

        productService.decreaseQuantity(productId, quantity);
        userService.addToBasket(user.getId(), productId, quantity);

        return ResponseEntity.ok(Map.of("message", "Товар добавлен в корзину"));
    }

    /**
     * Удаление товара из корзины пользователя.
     */
    @DeleteMapping("/user/basket/remove/{productId}")
    public ResponseEntity<?> removeFromBasket(
            @RequestHeader("Authorization") String token,
            @PathVariable Long productId) {

        Optional<User> userOptional = getUserFromToken(token);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Неавторизованный доступ"));
        }

        User user = userOptional.get();
        Map<Long, Product> userBasket = userService.getUserBasket(user.getId());

        if (!userBasket.containsKey(productId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Товар не найден в корзине"));
        }

        Product basketProduct = userBasket.get(productId);
        int quantityToReturn = basketProduct.getQuantity();

        productService.increaseQuantity(productId, quantityToReturn);
        userService.removeFromBasket(user.getId(), productId);

        return ResponseEntity.ok(Map.of(
                "message", "Товар удален из корзины",
                "quantity_returned", quantityToReturn
        ));
    }

    /**
     * Очистка корзины пользователя.
     */
    @DeleteMapping("/user/basket/clear")
    public ResponseEntity<?> clearUserBasket(@RequestHeader("Authorization") String token) {
        Optional<User> userOptional = getUserFromToken(token);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Неавторизованный доступ"));
        }

        User user = userOptional.get();
        userService.clearUserBasket(user.getId());

        return ResponseEntity.ok(Map.of("message", "Корзина успешно очищена"));
    }

    /**
     * Создание новой категории (только ADMIN).
     */
    @PostMapping("/admin/categories")
    public ResponseEntity<?> createCategory(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CategoryDTO.CreateCategory createCategory) {

        if (!hasRole(token, Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Доступ запрещен. Требуется роль ADMIN"));
        }

        Category category = categoryMapper.toEntity(createCategory);
        Category savedCategory = categoryService.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryMapper.toDTO(savedCategory));
    }

    /**
     * Обновление категории (только ADMIN).
     */
    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<?> updateCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO.UpdateCategory updateCategory) {

        if (!hasRole(token, Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Доступ запрещен. Требуется роль ADMIN"));
        }

        Optional<Category> existingCategory = categoryService.findById(id);
        if (existingCategory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Category category = existingCategory.get();
        categoryMapper.updateEntityFromDTO(updateCategory, category);
        Category updatedCategory = categoryService.save(category);
        return ResponseEntity.ok(categoryMapper.toDTO(updatedCategory));
    }

    /**
     * Удаление категории (только ADMIN).
     */
    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<?> deleteCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        if (!hasRole(token, Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Доступ запрещен. Требуется роль ADMIN"));
        }

        if (!categoryService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Создание нового товара (только ADMIN).
     */
    @PostMapping("/admin/products")
    public ResponseEntity<?> createProduct(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ProductDTO.CreateProduct createProduct) {

        if (!hasRole(token, Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Доступ запрещен. Требуется роль ADMIN"));
        }

        Product product = productMapper.toEntity(createProduct);
        Product savedProduct = productService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.toDTO(savedProduct));
    }

    /**
     * Обновление товара (только ADMIN).
     */
    @PutMapping("/admin/products/{id}")
    public ResponseEntity<?> updateProduct(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO.UpdateProduct updateProduct) {

        if (!hasRole(token, Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Доступ запрещен. Требуется роль ADMIN"));
        }

        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Optional<Product> existingProduct = productService.findById(id);
        if (existingProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = existingProduct.get();
        productMapper.updateEntityFromDTO(updateProduct, product);
        product.setId(id);
        Product updatedProduct = productService.save(product);
        return ResponseEntity.ok(productMapper.toDTO(updatedProduct));
    }

    /**
     * Удаление товара (только ADMIN).
     */
    @DeleteMapping("/admin/products/{id}")
    public ResponseEntity<?> deleteProduct(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        if (!hasRole(token, Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Доступ запрещен. Требуется роль ADMIN"));
        }

        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получение всех пользователей (только ADMIN).
     */
    @GetMapping("/admin/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        if (!hasRole(token, Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Доступ запрещен. Требуется роль ADMIN"));
        }

        List<User> users = userService.showAllUser();
        return ResponseEntity.ok(userMapper.toDTOList(users));
    }

    /**
     * Получение статистики (только ADMIN).
     */
    @GetMapping("/admin/statistics")
    public ResponseEntity<?> getStatistics(@RequestHeader("Authorization") String token) {
        if (!hasRole(token, Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Доступ запрещен. Требуется роль ADMIN"));
        }

        List<User> users = userService.showAllUser();
        long totalProducts = productService.count();
        List<Category> categories = categoryService.findAll();

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalUsers", users.size());
        statistics.put("totalProducts", totalProducts);
        statistics.put("totalCategories", categories.size());
        statistics.put("activeSessions", activeSessions.size());

        return ResponseEntity.ok(statistics);
    }

    private String generateToken(String username, String password) {
        String credentials = username + ":" + password;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "";
        }
        return authHeader.substring(7);
    }

    private Optional<User> getUserFromToken(String token) {
        String cleanToken = extractToken(token);
        return Optional.ofNullable(activeSessions.get(cleanToken));
    }

    private boolean hasRole(String token, Role requiredRole) {
        Optional<User> userOptional = getUserFromToken(token);
        return userOptional.isPresent() && userOptional.get().getRole() == requiredRole;
    }
}