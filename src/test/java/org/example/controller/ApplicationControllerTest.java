package org.example.controller;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit тесты для ApplicationController
 */
@ExtendWith(MockitoExtension.class)
class ApplicationControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private UserService userService;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private UserMapper userMapper;

    private ApplicationController controller;

    @BeforeEach
    void setUp() {
        controller = new ApplicationController(productService, categoryService, userService,
                productMapper, categoryMapper, userMapper);
    }

    @Test
    void testRegisterUser_Success() {

        UserDTO userDTO = new UserDTO();
        userDTO.setUserName("newuser");
        userDTO.setPassword("password123");

        User newUser = new User();
        newUser.setUserName("newuser");
        newUser.setPassword("password123");
        newUser.setRole(Role.USER);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUserName("newuser");
        savedUser.setPassword("password123");
        savedUser.setRole(Role.USER);

        UserDTO.UserInfo userInfo = new UserDTO.UserInfo(1L, "newuser", Role.USER);

        when(userService.isContainsUser("newuser")).thenReturn(false);
        when(userMapper.createUserFromDTO(any(UserDTO.class))).thenReturn(newUser);
        when(userService.saveUser(any(User.class))).thenReturn(savedUser);

        when(userMapper.toUserInfo(any(User.class))).thenReturn(userInfo);

        var response = controller.registerUser(userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Пользователь успешно зарегистрирован", response.getBody().get("message"));
        assertNotNull(response.getBody().get("token"));

        UserDTO.UserInfo responseUser = (UserDTO.UserInfo) response.getBody().get("user");
        assertEquals("newuser", responseUser.getUsername());
        assertEquals(Role.USER, responseUser.getRole());

        verify(userService, times(1)).isContainsUser("newuser");
        verify(userMapper, times(1)).createUserFromDTO(any(UserDTO.class));
        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {

        UserDTO userDTO = new UserDTO();
        userDTO.setUserName("existinguser");
        userDTO.setPassword("password123");

        when(userService.isContainsUser("existinguser")).thenReturn(true);

        var response = controller.registerUser(userDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Пользователь с таким именем уже существует", response.getBody().get("error"));

        verify(userService, times(1)).isContainsUser("existinguser");
        verify(userMapper, never()).toEntityFromAuth(any(UserDTO.class));
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    void testRegisterUser_WithAdminRole() {

        UserDTO userDTO = new UserDTO();
        userDTO.setUserName("admin");
        userDTO.setPassword("admin123");
        userDTO.setRole(Role.ADMIN);

        User newUser = new User();
        newUser.setUserName("admin");
        newUser.setPassword("admin123");
        newUser.setRole(Role.ADMIN);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUserName("admin");
        savedUser.setPassword("admin123");
        savedUser.setRole(Role.ADMIN);

        UserDTO.UserInfo userInfo = new UserDTO.UserInfo(1L, "admin", Role.ADMIN);

        when(userService.isContainsUser("admin")).thenReturn(false);
        when(userMapper.createUserFromDTO(any(UserDTO.class))).thenReturn(newUser);
        when(userService.saveUser(any(User.class))).thenReturn(savedUser);

        when(userMapper.toUserInfo(any(User.class))).thenReturn(userInfo);

        var response = controller.registerUser(userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserDTO.UserInfo responseUser = (UserDTO.UserInfo) response.getBody().get("user");
        assertEquals("admin", responseUser.getUsername());
        assertEquals(Role.ADMIN, responseUser.getRole());
    }

    @Test
    void testLogin_Success() {

        UserDTO.LoginRequest loginRequest = new UserDTO.LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("testpass");

        User user = new User();
        user.setId(1L);
        user.setUserName("testuser");
        user.setPassword("testpass");
        user.setRole(Role.USER);

        UserDTO.UserInfo userInfo = new UserDTO.UserInfo();
        userInfo.setId(1L);
        userInfo.setUsername("testuser");
        userInfo.setRole(Role.USER);

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userMapper.toUserInfo(any(User.class))).thenReturn(userInfo);

        var response = controller.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Успешный вход в систему", response.getBody().get("message"));
        assertNotNull(response.getBody().get("token"));

        UserDTO.UserInfo responseUser = (UserDTO.UserInfo) response.getBody().get("user");
        assertEquals("testuser", responseUser.getUsername());
        assertEquals(Role.USER, responseUser.getRole());
    }

    @Test
    void testLogin_InvalidUsername() {

        UserDTO.LoginRequest loginRequest = new UserDTO.LoginRequest();
        loginRequest.setUsername("wronguser");
        loginRequest.setPassword("testpass");

        when(userService.findByUsername("wronguser")).thenReturn(Optional.empty());

        var response = controller.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Неверное имя пользователя или пароль", response.getBody().get("error"));
    }

    @Test
    void testLogin_WrongPassword() {

        UserDTO.LoginRequest loginRequest = new UserDTO.LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpass");

        User user = new User();
        user.setId(1L);
        user.setUserName("testuser");
        user.setPassword("correctpass"); // Пароль в БД другой

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        var response = controller.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Неверное имя пользователя или пароль", response.getBody().get("error"));
    }

    @Test
    void testLogout_Success() {

        String token = "Bearer " + Base64.getEncoder().encodeToString("user:pass".getBytes());

        UserDTO.LoginRequest loginRequest = new UserDTO.LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("pass");

        User user = new User();
        user.setUserName("user");
        user.setPassword("pass");
        user.setRole(Role.USER);

        UserDTO.UserInfo userInfo = new UserDTO.UserInfo();
        userInfo.setUsername("user");
        userInfo.setRole(Role.USER);

        when(userService.findByUsername("user")).thenReturn(Optional.of(user));
        when(userMapper.toUserInfo(any(User.class))).thenReturn(userInfo);

        controller.login(loginRequest);

        var response = controller.logout(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Успешный выход из системы", response.getBody().get("message"));
    }

    @Test
    void testLogout_SessionNotFound() {

        String token = "Bearer invalidtoken";

        var response = controller.logout(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Сессия не найдена", response.getBody().get("message"));
    }

    @Test
    void testGetProductById_Found() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(100);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Test Product");
        productDTO.setPrice(100);

        when(productService.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ResponseEntity<ProductDTO> response = controller.getProductById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, (long) Objects.requireNonNull(response.getBody()).getId());
        assertEquals("Test Product", response.getBody().getName());
    }

    @Test
    void testGetCategoryById_Found() {

        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Test Category");

        when(categoryService.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        ResponseEntity<CategoryDTO> response = controller.getCategoryById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, (long) Objects.requireNonNull(response.getBody()).getId());
        assertEquals("Test Category", response.getBody().getName());
    }

    @Test
    void testGetCategoryById_NotFound() {

        when(categoryService.findById(999L)).thenReturn(Optional.empty());

        var response = controller.getCategoryById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllProducts() {

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(999);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Phone");
        product2.setPrice(499);

        ProductDTO productDTO1 = new ProductDTO();
        productDTO1.setId(1L);
        productDTO1.setName("Laptop");
        productDTO1.setPrice(999);

        ProductDTO productDTO2 = new ProductDTO();
        productDTO2.setId(2L);
        productDTO2.setName("Phone");
        productDTO2.setPrice(499);

        List<Product> products = Arrays.asList(product1, product2);
        List<ProductDTO> productDTOs = Arrays.asList(productDTO1, productDTO2);

        when(productService.findAllPaginated(0, 20)).thenReturn(products);
        when(productMapper.toDTOList(products)).thenReturn(productDTOs);
        when(productService.count()).thenReturn(100L);

        var response = controller.getAllProducts(0, 20);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(0, body.get("page"));
        assertEquals(20, body.get("size"));
        assertEquals(100L, body.get("totalProducts"));
        assertEquals(5L, body.get("totalPages")); // 100/20 = 5

        @SuppressWarnings("unchecked")
        List<ProductDTO> responseProducts = (List<ProductDTO>) body.get("products");
        assertEquals(2, responseProducts.size());
        assertEquals("Laptop", responseProducts.get(0).getName());
    }

    @Test
    void testGetAllProducts_InvalidPageAndSize() {

        when(productService.findAllPaginated(0, 20)).thenReturn(Collections.emptyList());
        when(productMapper.toDTOList(anyList())).thenReturn(Collections.emptyList());
        when(productService.count()).thenReturn(0L);

        var response1 = controller.getAllProducts(-1, 20);

        assertEquals(HttpStatus.OK, response1.getStatusCode());

        var response2 = controller.getAllProducts(0, 0);
        var response3 = controller.getAllProducts(0, 101);

        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());
    }

    @Test
    void testGetProductById_NotFound() {

        when(productService.findById(999L)).thenReturn(Optional.empty());

        var response = controller.getProductById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUserExists() {

        when(userService.isContainsUser("existinguser")).thenReturn(true);
        when(userService.isContainsUser("nonexistinguser")).thenReturn(false);

        var response1 = controller.userExists("existinguser");
        var response2 = controller.userExists("nonexistinguser");

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertTrue(response1.getBody().get("exists"));

        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertFalse(response2.getBody().get("exists"));
    }

    @Test
    void testGetCurrentUserProfile_Unauthorized() {

        String invalidToken = "Bearer invalid";

        var response = controller.getCurrentUserProfile(invalidToken);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Неавторизованный доступ", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testAddToBasket_Success() {

        String token = "Bearer " + Base64.getEncoder().encodeToString("user:pass".getBytes());
        Long productId = 1L;

        BasketDTO.AddToBasketRequest request = new BasketDTO.AddToBasketRequest();
        request.setQuantity(2);

        User user = new User();
        user.setId(1L);
        user.setUserName("user");
        user.setPassword("pass");
        user.setRole(Role.USER);

        Product product = new Product();
        product.setId(productId);
        product.setName("Product");
        product.setQuantity(10);

        UserDTO.LoginRequest loginRequest = new UserDTO.LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("pass");

        UserDTO.UserInfo userInfo = new UserDTO.UserInfo();
        userInfo.setId(1L);
        userInfo.setUsername("user");

        when(userService.findByUsername("user")).thenReturn(Optional.of(user));
        when(userMapper.toUserInfo(any(User.class))).thenReturn(userInfo);
        controller.login(loginRequest);

        when(productService.findById(productId)).thenReturn(Optional.of(product));
        when(productService.decreaseQuantity(productId, 2)).thenReturn(true);
        doNothing().when(userService).addToBasket(1L, productId, 2);

        var response = controller.addToBasket(token, productId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Товар добавлен в корзину", ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void testAddToBasket_InvalidQuantity() {

        String token = "Bearer " + Base64.getEncoder().encodeToString("user:pass".getBytes());

        BasketDTO.AddToBasketRequest request1 = new BasketDTO.AddToBasketRequest();
        request1.setQuantity(-1);

        BasketDTO.AddToBasketRequest request2 = new BasketDTO.AddToBasketRequest();
        request2.setQuantity(0);

        User user = new User();
        user.setId(1L);
        user.setUserName("user");
        user.setPassword("pass");
        user.setRole(Role.USER);

        UserDTO.UserInfo userInfo = new UserDTO.UserInfo();
        userInfo.setId(1L);
        userInfo.setUsername("user");

        UserDTO.LoginRequest loginRequest = new UserDTO.LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("pass");

        when(userService.findByUsername("user")).thenReturn(Optional.of(user));
        when(userMapper.toUserInfo(any(User.class))).thenReturn(userInfo);
        controller.login(loginRequest);

        var response1 = controller.addToBasket(token, 1L, request1);

        var response2 = controller.addToBasket(token, 1L, request2);

        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatusCode());
        assertEquals("Количество должно быть положительным", ((Map<?, ?>) response1.getBody()).get("error"));

        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        assertEquals("Количество должно быть положительным", ((Map<?, ?>) response2.getBody()).get("error"));
    }

    @Test
    void testCreateCategory_AdminAccess() {

        String token = "Bearer " + Base64.getEncoder().encodeToString("admin:admin123".getBytes());

        CategoryDTO.CreateCategory createCategory = new CategoryDTO.CreateCategory();
        createCategory.setName("New Category");

        User adminUser = new User();
        adminUser.setUserName("admin");
        adminUser.setPassword("admin123");
        adminUser.setRole(Role.ADMIN);

        Category category = new Category();
        category.setName("New Category");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("New Category");

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("New Category");

        UserDTO.LoginRequest loginRequest = new UserDTO.LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");

        UserDTO.UserInfo userInfo = new UserDTO.UserInfo();
        userInfo.setUsername("admin");
        userInfo.setRole(Role.ADMIN);

        when(userService.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(userMapper.toUserInfo(any(User.class))).thenReturn(userInfo);
        controller.login(loginRequest);

        when(categoryMapper.toEntity(any(CategoryDTO.CreateCategory.class))).thenReturn(category);
        when(categoryService.save(any(Category.class))).thenReturn(savedCategory);
        when(categoryMapper.toDTO(savedCategory)).thenReturn(categoryDTO);

        var response = controller.createCategory(token, createCategory);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        CategoryDTO responseBody = (CategoryDTO) response.getBody();
        assertEquals(1L, responseBody.getId());
        assertEquals("New Category", responseBody.getName());
    }

    @Test
    void testCreateCategory_NonAdminAccess() {

        String token = "Bearer " + Base64.getEncoder().encodeToString("user:user123".getBytes());

        CategoryDTO.CreateCategory createCategory = new CategoryDTO.CreateCategory();
        createCategory.setName("New Category");

        User regularUser = new User();
        regularUser.setUserName("user");
        regularUser.setPassword("user123");
        regularUser.setRole(Role.USER);

        UserDTO.LoginRequest loginRequest = new UserDTO.LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("user123");

        UserDTO.UserInfo userInfo = new UserDTO.UserInfo();
        userInfo.setUsername("user");
        userInfo.setRole(Role.USER);

        when(userService.findByUsername("user")).thenReturn(Optional.of(regularUser));
        when(userMapper.toUserInfo(regularUser)).thenReturn(userInfo);
        controller.login(loginRequest);

        var response = controller.createCategory(token, createCategory);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Доступ запрещен. Требуется роль ADMIN", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testGetStatistics_AdminOnly() {

        String token = "Bearer " + Base64.getEncoder().encodeToString("admin:admin123".getBytes());

        User adminUser = new User();
        adminUser.setUserName("admin");
        adminUser.setPassword("admin123");
        adminUser.setRole(Role.ADMIN);

        UserDTO.LoginRequest loginRequest = new UserDTO.LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");

        UserDTO.UserInfo userInfo = new UserDTO.UserInfo();
        userInfo.setUsername("admin");
        userInfo.setRole(Role.ADMIN);

        when(userService.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(userMapper.toUserInfo(any(User.class))).thenReturn(userInfo);
        controller.login(loginRequest);

        List<User> users = Arrays.asList(
                new User(), new User(), new User()
        );

        List<Category> categories = Arrays.asList(
                new Category(), new Category()
        );

        when(userService.showAllUser()).thenReturn(users);
        when(productService.count()).thenReturn(50L);
        when(categoryService.findAll()).thenReturn(categories);

        var response = controller.getStatistics(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) response.getBody();
        assertEquals(3, stats.get("totalUsers"));
        assertEquals(50L, stats.get("totalProducts"));
        assertEquals(2, stats.get("totalCategories"));
        assertNotNull(stats.get("activeSessions"));
    }

    @Test
    void testPrivateMethods() {
        try {
            var generateMethod = ApplicationController.class.getDeclaredMethod("generateToken", String.class, String.class);
            generateMethod.setAccessible(true);

            String username = "testuser";
            String password = "testpass";
            String token = (String) generateMethod.invoke(controller, username, password);

            assertNotNull(token);
            assertEquals("dGVzdHVzZXI6dGVzdHBhc3M=", token);

            var extractMethod = ApplicationController.class.getDeclaredMethod("extractToken", String.class);
            extractMethod.setAccessible(true);

            String authHeader = "Bearer " + token;
            String extracted = (String) extractMethod.invoke(controller, authHeader);

            assertEquals(token, extracted);

            String extracted2 = (String) extractMethod.invoke(controller, token);
            assertEquals("", extracted2);

            String extracted3 = (String) extractMethod.invoke(controller, (Object) null);
            assertEquals("", extracted3);

        } catch (Exception e) {
            fail("Не удалось протестировать приватные методы: " + e.getMessage());
        }
    }

    @Test
    void testGetProductsByCategory() {

        Long categoryId = 1L;

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setCategoryId(categoryId);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setCategoryId(categoryId);

        List<Product> products = Arrays.asList(product1, product2);

        ProductDTO productDTO1 = new ProductDTO();
        productDTO1.setId(1L);
        productDTO1.setName("Product 1");

        ProductDTO productDTO2 = new ProductDTO();
        productDTO2.setId(2L);
        productDTO2.setName("Product 2");

        List<ProductDTO> productDTOs = Arrays.asList(productDTO1, productDTO2);

        when(categoryService.findProductsByCategoryId(categoryId)).thenReturn(products);
        when(productMapper.toDTOList(products)).thenReturn(productDTOs);

        var response = controller.getProductsByCategory(categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ProductDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }

    @Test
    void testGetUserBasket() {

        String token = "Bearer " + Base64.getEncoder().encodeToString("user:pass".getBytes());

        User user = new User();
        user.setId(1L);
        user.setUserName("user");
        user.setPassword("pass");

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setQuantity(2);
        product1.setPrice(100);
        product1.setCategoryId(1L);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setQuantity(1);
        product2.setPrice(200);
        product2.setCategoryId(1L);

        Map<Long, Product> basket = new HashMap<>();
        basket.put(1L, product1);
        basket.put(2L, product2);

        UserDTO.LoginRequest loginRequest = new UserDTO.LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("pass");

        UserDTO.UserInfo userInfo = new UserDTO.UserInfo();
        userInfo.setId(1L);
        userInfo.setUsername("user");

        when(userService.findByUsername("user")).thenReturn(Optional.of(user));
        when(userMapper.toUserInfo(any(User.class))).thenReturn(userInfo);
        controller.login(loginRequest);

        when(userService.getUserBasket(1L)).thenReturn(basket);

        var response = controller.getUserBasket(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BasketDTO basketDTO = (BasketDTO) response.getBody();
        assertNotNull(basketDTO);
        assertEquals(2, basketDTO.getItems().size());
    }

    @Test
    void testUpdateProduct() {

        String token = "Bearer " + Base64.getEncoder().encodeToString("admin:admin123".getBytes());
        Long productId = 1L;

        ProductDTO.UpdateProduct updateProduct = new ProductDTO.UpdateProduct();
        updateProduct.setName("Updated Product");
        updateProduct.setQuantity(50);
        updateProduct.setPrice(150);
        updateProduct.setCategoryId(2L);

        User adminUser = new User();
        adminUser.setUserName("admin");
        adminUser.setPassword("admin123");
        adminUser.setRole(Role.ADMIN);

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Old Product");
        existingProduct.setQuantity(30);
        existingProduct.setPrice(100);
        existingProduct.setCategoryId(1L);

        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName("Updated Product");
        updatedProduct.setQuantity(50);
        updatedProduct.setPrice(150);
        updatedProduct.setCategoryId(2L);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(productId);
        productDTO.setName("Updated Product");
        productDTO.setQuantity(50);
        productDTO.setPrice(150);
        productDTO.setCategoryId(2L);

        UserDTO.LoginRequest loginRequest = new UserDTO.LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");

        UserDTO.UserInfo userInfo = new UserDTO.UserInfo();
        userInfo.setUsername("admin");
        userInfo.setRole(Role.ADMIN);

        when(userService.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(userMapper.toUserInfo(any(User.class))).thenReturn(userInfo);
        controller.login(loginRequest);

        when(productService.existsById(productId)).thenReturn(true);
        when(productService.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productService.save(any(Product.class))).thenReturn(updatedProduct);
        when(productMapper.toDTO(updatedProduct)).thenReturn(productDTO);

        var response = controller.updateProduct(token, productId, updateProduct);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ProductDTO responseBody = (ProductDTO) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Updated Product", responseBody.getName());
        assertEquals(150, responseBody.getPrice());
    }
}