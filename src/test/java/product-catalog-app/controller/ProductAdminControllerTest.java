package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productCatalogService.controller.ProductAdminController;
import com.productCatalogService.dto.ProductDTO;
import com.productCatalogService.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductAdminControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductAdminController productAdminController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productAdminController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createProduct_WithValidRequest_ShouldReturnCreated() throws Exception {
        // Arrange
        ProductDTO.CreateProduct createProduct = ProductDTO.CreateProduct.builder()
                .name("New Product")
                .quantity(10)
                .price(100)  // int, а не BigDecimal
                .categoryId(1L)
                .build();

        ProductDTO productDTO = ProductDTO.builder()
                .id(1L)
                .name("New Product")
                .quantity(10)
                .price(100)
                .categoryId(1L)
                .build();

        when(productService.createProduct(eq("Bearer admin-token"), any()))
                .thenReturn(productDTO);

        // Act & Assert
        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.categoryId").value(1));

        verify(productService, times(1)).createProduct(eq("Bearer admin-token"), any());
    }

    @Test
    void updateProduct_WithValidRequest_ShouldReturnOk() throws Exception {
        // Arrange
        ProductDTO.UpdateProduct updateProduct = ProductDTO.UpdateProduct.builder()
                .name("Updated Product")
                .quantity(20)
                .price(150)
                .categoryId(2L)
                .build();

        ProductDTO productDTO = ProductDTO.builder()
                .id(1L)
                .name("Updated Product")
                .quantity(20)
                .price(150)
                .categoryId(2L)
                .build();

        when(productService.updateProduct(eq("Bearer admin-token"), eq(1L), any()))
                .thenReturn(productDTO);

        // Act & Assert
        mockMvc.perform(put("/api/admin/products/1")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(150))
                .andExpect(jsonPath("$.quantity").value(20))
                .andExpect(jsonPath("$.categoryId").value(2));

        verify(productService, times(1)).updateProduct(eq("Bearer admin-token"), eq(1L), any());
    }

    @Test
    void deleteProduct_WithValidId_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct("Bearer admin-token", 1L);

        // Act & Assert
        mockMvc.perform(delete("/api/admin/products/1")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct("Bearer admin-token", 1L);
    }

    @Test
    void createProduct_WithoutAuthorization_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        ProductDTO.CreateProduct createProduct = ProductDTO.CreateProduct.builder()
                .name("New Product")
                .quantity(10)
                .price(100)
                .categoryId(1L)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProduct)))
                .andExpect(status().is4xxClientError());

        verify(productService, never()).createProduct(any(), any());
    }

    @Test
    void createProduct_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Test 1: Empty name
        ProductDTO.CreateProduct invalidRequest1 = ProductDTO.CreateProduct.builder()
                .name("")  // Empty - violates @NotBlank
                .quantity(10)
                .price(100)
                .categoryId(1L)
                .build();

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest1)))
                .andExpect(status().isBadRequest());

        // Test 2: Negative price
        ProductDTO.CreateProduct invalidRequest2 = ProductDTO.CreateProduct.builder()
                .name("Valid Product")
                .quantity(10)
                .price(-1)  // Negative - violates @Min(value = 0)
                .categoryId(1L)
                .build();

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest2)))
                .andExpect(status().isBadRequest());

        // Test 3: Negative quantity
        ProductDTO.CreateProduct invalidRequest3 = ProductDTO.CreateProduct.builder()
                .name("Valid Product")
                .quantity(-5)  // Negative - violates @Min(value = 0)
                .price(100)
                .categoryId(1L)
                .build();

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest3)))
                .andExpect(status().isBadRequest());

        // Test 4: Null categoryId
        Map<String, Object> invalidRequest4 = new HashMap<>();
        invalidRequest4.put("name", "Valid Product");
        invalidRequest4.put("quantity", 10);
        invalidRequest4.put("price", 100);
        invalidRequest4.put("categoryId", null);  // Null - violates @NotNull

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest4)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(), any());
    }

    @Test
    void updateProduct_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Test with invalid data
        ProductDTO.UpdateProduct invalidRequest = ProductDTO.UpdateProduct.builder()
                .name("")  // Empty - violates @NotBlank
                .quantity(-1)  // Negative - violates @Min(value = 0)
                .price(-1)  // Negative - violates @Min(value = 0)
                .categoryId(null)  // Null - violates @NotNull
                .build();

        mockMvc.perform(put("/api/admin/products/1")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).updateProduct(any(), any(), any());
    }
}