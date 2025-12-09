package controller;

import com.productCatalogService.controller.ProductController;
import com.productCatalogService.dto.ProductDTO;
import com.productCatalogService.dto.ProductPageDTO;
import com.productCatalogService.service.CategoryService;
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
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    private ProductDTO product1;
    private ProductDTO product2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        product1 = ProductDTO.builder()
                .id(1L)
                .name("Laptop")
                .price(1500)
                .categoryId(1L)
                .build();

        product2 = ProductDTO.builder()
                .id(2L)
                .name("Book")
                .price(50)
                .categoryId(2L)
                .build();
    }

    @Test
    void getAllProducts_WithDefaultPagination_ShouldReturnProducts() throws Exception {
        // Arrange
        ProductPageDTO productPageDTO = new ProductPageDTO();
        productPageDTO.setProducts(Arrays.asList(product1, product2));
        productPageDTO.setPage(0);
        productPageDTO.setSize(20);
        productPageDTO.setTotalProducts(2L);
        productPageDTO.setTotalPages(1);
        productPageDTO.setHasNext(false);
        productPageDTO.setHasPrevious(false);

        when(productService.getPaginatedProducts(0, 20)).thenReturn(productPageDTO);

        // Act & Assert
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].name").value("Laptop"))
                .andExpect(jsonPath("$.products[1].name").value("Book"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));

        verify(productService, times(1)).getPaginatedProducts(0, 20);
    }

    @Test
    void getAllProducts_WithCustomPagination_ShouldReturnProducts() throws Exception {
        // Arrange
        ProductPageDTO productPageDTO = new ProductPageDTO();
        productPageDTO.setProducts(List.of(product1));
        productPageDTO.setPage(1);
        productPageDTO.setSize(5);
        productPageDTO.setTotalProducts(10L);
        productPageDTO.setTotalPages(2);
        productPageDTO.setHasNext(false);
        productPageDTO.setHasPrevious(true);

        when(productService.getPaginatedProducts(1, 5)).thenReturn(productPageDTO);

        // Act & Assert
        mockMvc.perform(get("/api/products")
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products[0].name").value("Laptop"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.hasPrevious").value(true))
                .andExpect(jsonPath("$.hasNext").value(false));

        verify(productService, times(1)).getPaginatedProducts(1, 5);
    }

    @Test
    void getProductById_WithValidId_ShouldReturnProduct() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(product1);

        // Act & Assert
        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1500));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void getProductsByCategoryId_ShouldReturnProducts() throws Exception {
        // Arrange
        List<ProductDTO> products = Arrays.asList(product1);
        when(categoryService.getProductsByCategoryIdDto(1L)).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/products/category/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].categoryId").value(1));

        verify(categoryService, times(1)).getProductsByCategoryIdDto(1L);
    }
}
