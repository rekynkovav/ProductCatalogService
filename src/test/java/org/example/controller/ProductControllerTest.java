package org.example.controller;

import org.example.dto.ProductDTO;
import org.example.dto.ProductPageDTO;
import org.example.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private ProductController productController;
    private ProductAdminController productAdminController;

    @BeforeEach
    void setUp() {
        productController = new ProductController(productService);
        productAdminController = new ProductAdminController(productService);
    }

    @Test
    void testGetAllProducts() {
        ProductPageDTO pageDTO = new ProductPageDTO();
        pageDTO.setPage(0);
        pageDTO.setSize(20);
        pageDTO.setTotalProducts(100);
        pageDTO.setTotalPages(5);

        ProductDTO productDTO1 = new ProductDTO();
        productDTO1.setId(1L);
        productDTO1.setName("Laptop");

        ProductDTO productDTO2 = new ProductDTO();
        productDTO2.setId(2L);
        productDTO2.setName("Phone");

        pageDTO.setProducts(Arrays.asList(productDTO1, productDTO2));

        when(productService.getPaginatedProducts(0, 20)).thenReturn(pageDTO);

        ResponseEntity<ProductPageDTO> response = productController.getAllProducts(0, 20);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getProducts().size());
        assertEquals(100, response.getBody().getTotalProducts());
    }

    @Test
    void testGetProductById() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Test Product");
        productDTO.setPrice(100);

        when(productService.getProductById(1L)).thenReturn(productDTO);

        ResponseEntity<ProductDTO> response = productController.getProductById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Product", response.getBody().getName());
        assertEquals(100, response.getBody().getPrice());
    }

    @Test
    void testGetProductsByCategoryId() {
        ProductDTO productDTO1 = new ProductDTO();
        productDTO1.setId(1L);
        productDTO1.setName("Product 1");

        ProductDTO productDTO2 = new ProductDTO();
        productDTO2.setId(2L);
        productDTO2.setName("Product 2");

        List<ProductDTO> products = Arrays.asList(productDTO1, productDTO2);

        when(productService.getProductsByCategoryId(1L)).thenReturn(products);

        ResponseEntity<List<ProductDTO>> response = productController.getProductsByCategoryId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testCreateProduct_Success() {
        String token = "Bearer admintoken";
        ProductDTO.CreateProduct createProduct = new ProductDTO.CreateProduct();
        createProduct.setName("New Product");

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("New Product");

        when(productService.createProduct(token, createProduct)).thenReturn(productDTO);

        ResponseEntity<?> response = productAdminController.createProduct(token, createProduct);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        ProductDTO responseBody = (ProductDTO) response.getBody();
        assertEquals(1L, responseBody.getId());
        assertEquals("New Product", responseBody.getName());
    }

    @Test
    void testCreateProduct_AccessDenied() {
        String token = "Bearer usertoken";
        ProductDTO.CreateProduct createProduct = new ProductDTO.CreateProduct();

        when(productService.createProduct(token, createProduct))
                .thenThrow(new SecurityException("Доступ запрещен"));

        ResponseEntity<?> response = productAdminController.createProduct(token, createProduct);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Доступ запрещен", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testUpdateProduct_Success() {
        String token = "Bearer admintoken";
        Long productId = 1L;
        ProductDTO.UpdateProduct updateProduct = new ProductDTO.UpdateProduct();
        updateProduct.setName("Updated Product");

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(productId);
        productDTO.setName("Updated Product");

        when(productService.updateProduct(token, productId, updateProduct)).thenReturn(productDTO);

        ResponseEntity<?> response = productAdminController.updateProduct(token, productId, updateProduct);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Product", ((ProductDTO) response.getBody()).getName());
    }

    @Test
    void testUpdateProduct_NotFound() {
        String token = "Bearer admintoken";
        Long productId = 999L;
        ProductDTO.UpdateProduct updateProduct = new ProductDTO.UpdateProduct();

        when(productService.updateProduct(token, productId, updateProduct))
                .thenThrow(new IllegalArgumentException("Продукт не найден"));

        ResponseEntity<?> response = productAdminController.updateProduct(token, productId, updateProduct);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteProduct_Success() {
        String token = "Bearer admintoken";
        Long productId = 1L;

        ResponseEntity<?> response = productAdminController.deleteProduct(token, productId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}