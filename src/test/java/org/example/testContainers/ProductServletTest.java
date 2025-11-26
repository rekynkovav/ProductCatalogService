package org.example.testContainers;

import org.example.model.dto.ProductDTO;
import org.example.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.servlet.ProductServlet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;

import static org.mockito.Mockito.*;

public class ProductServletTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ProductService productService;

    private ProductServlet productServlet;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        productServlet = new ProductServlet();

        // Используем рефлексию для установки зависимостей
        var productServiceField = ProductServlet.class.getDeclaredField("productService");
        productServiceField.setAccessible(true);
        productServiceField.set(productServlet, productService);

        var objectMapperField = ProductServlet.class.getDeclaredField("objectMapper");
        objectMapperField.setAccessible(true);
        objectMapperField.set(productServlet, new ObjectMapper());

        objectMapper = new ObjectMapper();
    }

    @Test
    public void testDoGetAllProducts() throws Exception {
        // Given
        when(request.getPathInfo()).thenReturn("/");
        when(request.getMethod()).thenReturn("GET");

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // When
        productServlet.doGet(request, response);

        // Then
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        writer.flush();
        assert stringWriter.toString().contains("success");
    }

    @Test
    public void testDoPostValidProduct() throws Exception {
        // Given
        when(request.getPathInfo()).thenReturn("/");
        when(request.getMethod()).thenReturn("POST");

        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test Product");
        productDTO.setDescription("Test Description");
        productDTO.setPrice(new BigDecimal("99.99"));
        productDTO.setQuantity(10);
        productDTO.setCategory("Test Category");

        String jsonRequest = objectMapper.writeValueAsString(productDTO);
        when(request.getInputStream()).thenReturn(
                new MockServletInputStream(new ByteArrayInputStream(jsonRequest.getBytes()))
        );

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // When
        productServlet.doPost(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        writer.flush();
        assert stringWriter.toString().contains("Product created successfully");
    }

    @Test
    public void testDoPostInvalidProduct() throws Exception {
        // Given
        when(request.getPathInfo()).thenReturn("/");
        when(request.getMethod()).thenReturn("POST");

        ProductDTO productDTO = new ProductDTO(); // Невалидный продукт
        String jsonRequest = objectMapper.writeValueAsString(productDTO);
        when(request.getInputStream()).thenReturn(
                new MockServletInputStream(new ByteArrayInputStream(jsonRequest.getBytes()))
        );

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // When
        productServlet.doPost(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        writer.flush();
        assert stringWriter.toString().contains("error");
    }

    // Вспомогательный класс для мока InputStream
    private static class MockServletInputStream extends ServletInputStream {
        private final InputStream inputStream;

        public MockServletInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            try {
                return inputStream.available() == 0;
            } catch (IOException e) {
                return true;
            }
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // Not implemented for tests
        }
    }
}
