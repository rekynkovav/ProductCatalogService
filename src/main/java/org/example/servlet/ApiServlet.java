package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.context.ApplicationContext;
import org.example.model.entity.Category;
import org.example.model.entity.Product;
import org.example.model.entity.User;
import org.example.repository.AspectRepository;
import org.example.service.ProductService;
import org.example.service.SecurityService;
import org.example.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Главный сервлет API, обрабатывающий все HTTP-запросы приложения.
 * Предоставляет эндпоинты для аутентификации, управления товарами, корзиной и метриками.
 */
@WebServlet("/api/*")
public class ApiServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ProductService productService;
    private UserService userService;
    private SecurityService securityService;
    private AspectRepository aspectRepository;

    @Override
    public void init() throws ServletException {
         productService = ApplicationContext.getInstance().getBean(ProductService.class);
         userService = ApplicationContext.getInstance().getBean(UserService.class);
         securityService = ApplicationContext.getInstance().getBean(SecurityService.class);
         aspectRepository = ApplicationContext.getInstance().getBean(AspectRepository.class);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String requestBody = req.getReader().lines().collect(Collectors.joining());

        switch (pathInfo) {
            case "/login":
                handleLogin(req, resp, requestBody);
                break;
            case "/register":
                handleRegister(req, resp, requestBody);
                break;
            case "/products":
                handleAddProduct(req, resp, requestBody);
                break;
            case "/basket/add":
                handleAddToBasket(req, resp, requestBody);
                break;
            case "/logout":
                handleLogout(req, resp);
                break;
            default:
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        switch (pathInfo) {
            case "/products":
                handleGetProducts(req, resp);
                break;
            case "/basket":
                handleGetBasket(req, resp);
                break;
            case "/metrics":
                handleGetMetrics(req, resp);
                break;
            case "/products/filter":
                handleFilterProducts(req, resp);
                break;
            case "/user/profile":
                handleGetUserProfile(req, resp);
                break;
            default:
                sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String requestBody = req.getReader().lines().collect(Collectors.joining());

        if ("/products".equals(pathInfo)) {
            handleUpdateProduct(req, resp, requestBody);
        } else {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo != null && pathInfo.startsWith("/products/")) {
            String productId = pathInfo.substring("/products/".length());
            handleDeleteProduct(req, resp, productId);
        }else {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }


    /**
     * Получение профиля пользователя
     */
    private void handleGetUserProfile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
                return;
            }
            sendSuccess(resp, "User profile retrieved", user);
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving user profile: " + e.getMessage());
        }
    }
    /**
     * Обработка выхода пользователя
     */
    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User user = (User) req.getSession().getAttribute("user");
            if (user != null) {
                // Сохраняем метрику выхода
                aspectRepository.incrementMetric(user.getId(), "LOGOUT_COUNT");
                req.getSession().invalidate();
            }
            sendSuccess(resp, "Logout successful");
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during logout: " + e.getMessage());
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp, String requestBody) throws IOException {
        try {
            LoginRequest loginRequest = objectMapper.readValue(requestBody, LoginRequest.class);

            boolean isAuthenticated = securityService.verificationUser(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            if (isAuthenticated) {
                req.getSession().setAttribute("user", securityService.getThisUser());
                sendSuccess(resp, "Login successful", securityService.getThisUser());
            } else {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
            }
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request format");
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp, String requestBody) throws IOException {
        try {
            RegisterRequest registerRequest = objectMapper.readValue(requestBody, RegisterRequest.class);

            securityService.registerUser(
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getRole()
            );

            sendSuccess(resp, "Registration successful");
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleGetProducts(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pageParam = req.getParameter("page");
            String categoryParam = req.getParameter("category");

            int page = pageParam != null ? Integer.parseInt(pageParam) : 0;

            List<Product> products;
            if (categoryParam != null) {
                Category category = Category.valueOf(categoryParam.toUpperCase());
                products = productService.searchCategory(category);
            } else {
                products = productService.getAllProduct(page);
            }

            sendSuccess(resp, "Products retrieved", products);
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void handleAddToBasket(HttpServletRequest req, HttpServletResponse resp, String requestBody) throws IOException {
        try {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
                return;
            }

            BasketRequest basketRequest = objectMapper.readValue(requestBody, BasketRequest.class);
            productService.addBasket(
                    user.getId(),
                    basketRequest.getProductId(),
                    basketRequest.getQuantity()
            );

            sendSuccess(resp, "Product added to basket");
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleAddProduct(HttpServletRequest req, HttpServletResponse resp, String requestBody) throws IOException {
        try {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
                return;
            }

            if (!user.getRole().name().equals("ADMIN")) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only administrators can add products");
                return;
            }

            ProductRequest productRequest = objectMapper.readValue(requestBody, ProductRequest.class);

            Product product = new Product(
                    productRequest.getName(),
                    productRequest.getQuantity(),
                    productRequest.getPrice(),
                    Category.valueOf(productRequest.getCategory().toUpperCase())
            );

            productService.saveProduct(product);

            // Сохраняем метрику добавления товара
            aspectRepository.incrementMetric(user.getId(), "PRODUCT_ADD_COUNT");

            sendSuccess(resp, "Product added successfully", product);

        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid category: " + e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error adding product: " + e.getMessage());
        }
    }

    private void handleGetBasket(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
                return;
            }

            Map<Long, Product> basketMap = userService.getUserBasket(user.getId());

            if (basketMap.isEmpty()) {
                sendSuccess(resp, "Basket is empty");
            } else {
                // Рассчитываем общую сумму
                double total = basketMap.values().stream()
                        .mapToDouble(product -> product.getPrice() * product.getQuantity())
                        .sum();

                BasketResponse basketResponse = new BasketResponse(
                        true,
                        "Basket retrieved successfully",
                        basketMap,
                        total
                );

                sendSuccess(resp, "Basket retrieved successfully", basketResponse);
            }
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving basket: " + e.getMessage());
        }
    }

    private void handleFilterProducts(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String categoryParam = req.getParameter("category");
            if (categoryParam == null || categoryParam.trim().isEmpty()) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Category parameter is required");
                return;
            }

            Category category = Category.valueOf(categoryParam.toUpperCase());
            List<Product> products = productService.searchCategory(category);

            sendSuccess(resp, "Filtered products retrieved", products);
        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid category. Available categories: " +
                                                                java.util.Arrays.toString(Category.values()));
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error filtering products: " + e.getMessage());
        }
    }

    private void handleUpdateProduct(HttpServletRequest req, HttpServletResponse resp, String requestBody) throws IOException {
        try {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
                return;
            }

            if (!user.getRole().name().equals("ADMIN")) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only administrators can update products");
                return;
            }

            ProductRequest productRequest = objectMapper.readValue(requestBody, ProductRequest.class);

            if (productRequest.getId() == null) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Product ID is required for update");
                return;
            }

            Product product = new Product(
                    productRequest.getName(),
                    productRequest.getQuantity(),
                    productRequest.getPrice(),
                    Category.valueOf(productRequest.getCategory().toUpperCase())
            );
            product.setId(productRequest.getId());

            productService.updateProduct(product);

            // Сохраняем метрику обновления товара
            aspectRepository.incrementMetric(user.getId(), "PRODUCT_UPDATE_COUNT");

            sendSuccess(resp, "Product updated successfully", product);

        } catch (IllegalArgumentException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid category: " + e.getMessage());
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating product: " + e.getMessage());
        }
    }

    private void handleDeleteProduct(HttpServletRequest req, HttpServletResponse resp, String productId) throws IOException {
        try {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
                return;
            }

            if (!user.getRole().name().equals("ADMIN")) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only administrators can delete products");
                return;
            }

            long id = Long.parseLong(productId);
            productService.deleteProductById(id);

            // Сохраняем метрику удаления товара
            aspectRepository.incrementMetric(user.getId(), "PRODUCT_DELETE_COUNT");

            sendSuccess(resp, "Product deleted successfully");

        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID format");
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting product: " + e.getMessage());
        }
    }

    private void sendSuccess(HttpServletResponse resp, String message) throws IOException {
        sendSuccess(resp, message, null);
    }

    private void sendSuccess(HttpServletResponse resp, String message, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        ApiResponse response = new ApiResponse(true, message, data);
        objectMapper.writeValue(resp.getWriter(), response);
    }

    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(statusCode);

        ApiResponse response = new ApiResponse(false, message);
        objectMapper.writeValue(resp.getWriter(), response);
    }

    private void handleGetMetrics(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null) {
                sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
                return;
            }

            // Проверка прав администратора
            if (!user.getRole().name().equals("ADMIN")) {
                sendError(resp, HttpServletResponse.SC_FORBIDDEN, "Only administrators can view metrics");
                return;
            }

            Map<String, Integer> allMetrics = aspectRepository.getAllMetrics();


            // Формируем ответ
            MetricsResponse metricsResponse = new MetricsResponse(
                    true,
                    "Metrics retrieved successfully",
                    allMetrics
            );

            sendSuccess(resp, "Metrics retrieved successfully", metricsResponse);

        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving metrics: " + e.getMessage());
        }
    }
}