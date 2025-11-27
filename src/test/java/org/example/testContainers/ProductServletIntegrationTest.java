package org.example.testContainers;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import org.example.context.ApplicationContext;
import org.example.model.entity.Category;
import org.example.model.entity.Product;
import org.example.service.ProductService;
import org.example.servlet.ProductServlet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Интеграционные тесты для ProductServlet с использованием TestContainers
 */
@Testcontainers
class ProductServletIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private ProductServlet productServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;
    private ObjectMapper objectMapper;
    private ProductService productService;

    @BeforeEach
    void setUp() throws Exception {
        System.setProperty("DB_URL", postgres.getJdbcUrl());
        System.setProperty("DB_USERNAME", postgres.getUsername());
        System.setProperty("DB_PASSWORD", postgres.getPassword());

        productServlet = new ProductServlet();
        productServlet.init();

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        objectMapper = new ObjectMapper();
        productService = ApplicationContext.getInstance().getBean(ProductService.class);

        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @AfterEach
    void tearDown() {
        ApplicationContext.clear();
    }

    @Test
    void testDoGetAllProducts() throws Exception {
        // Given
        when(request.getPathInfo()).thenReturn("/");

        // Добавляем тестовый продукт
        Product product = new Product("Test Product", 10, 100, Category.ELECTRONICS);
        productService.saveProduct(product);

        // When
        productServlet.doGet(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_OK);
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Test Product"));
    }

    @Test
    void testDoGetProductById() throws Exception {
        // Given
        Product product = new Product("Specific Product", 5, 50, Category.BOOKS);
        Product savedProduct = productService.saveProduct(product);

        when(request.getPathInfo()).thenReturn("/" + savedProduct.getId());

        // When
        productServlet.doGet(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_OK);
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Specific Product"));
    }

    @Test
    void testDoGetProductNotFound() throws Exception {
        // Given
        when(request.getPathInfo()).thenReturn("/999");

        // When
        productServlet.doGet(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Product not found"));
    }

    @Test
    void testDoPostCreateProduct() throws Exception {
        // Given
        when(request.getPathInfo()).thenReturn(null);
        String productJson = "{\"name\":\"New Product\",\"quantity\":10,\"price\":100,\"category\":\"ELECTRONICS\"}";
        when(request.getInputStream()).thenReturn(new MockServletInputStream(productJson));

        // When
        productServlet.doPost(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Product created successfully"));
        assertTrue(responseContent.contains("New Product"));
    }

    @Test
    void testDoPostInvalidProduct() throws Exception {
        // Given
        when(request.getPathInfo()).thenReturn(null);
        String invalidProductJson = "{\"name\":\"\",\"quantity\":-1,\"price\":-100,\"category\":\"\"}";
        when(request.getInputStream()).thenReturn(new MockServletInputStream(invalidProductJson));

        // When
        productServlet.doPost(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("error"));
    }

    @Test
    void testDoDeleteProduct() throws Exception {
        Product product = new Product("To Delete", 1, 10, Category.CLOTHING);
        Product savedProduct = productService.saveProduct(product);

        when(request.getPathInfo()).thenReturn("/" + savedProduct.getId());

        productServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Product deleted successfully"));

        Optional<Product> deletedProduct = productService.findById(savedProduct.getId());
        assertFalse(deletedProduct.isPresent());
    }

    private static class MockServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public MockServletInputStream(String content) {
            this.inputStream = new ByteArrayInputStream(content.getBytes());
        }

        @Override
        public int read() {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }
    }
}