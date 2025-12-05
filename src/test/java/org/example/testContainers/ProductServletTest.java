package org.example.testContainers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.model.dto.ProductDTO;
import org.example.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.servlet.ProductServlet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
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
        extracted(jsonRequest);


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

    private void extracted(String jsonRequest) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(jsonRequest));
        when(request.getReader()).thenReturn(reader);
    }

    @Test
    public void testDoPostInvalidProduct() throws Exception {
        // Given
        when(request.getPathInfo()).thenReturn("/");
        when(request.getMethod()).thenReturn("POST");

        ProductDTO productDTO = new ProductDTO(); // Невалидный продукт
        String jsonRequest = objectMapper.writeValueAsString(productDTO);
        extracted(jsonRequest);

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
}
