import org.example.exception.ResourceNotFoundException;
import org.example.mapper.ProductMapper;
import org.example.model.entity.Product;
import org.example.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

/**
 * Unit тесты для ProductService.
 * Тестирует бизнес-логику сервиса продуктов.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see ProductService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductDto testProductDto;

    /**
     * Настройка тестового окружения перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(99.99))
                .quantity(10)
                .build();

        testProductDto = ProductDto.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(99.99))
                .quantity(10)
                .categoryId(1L)
                .build();
    }

    /**
     * Тест получения всех продуктов.
     */
    @Test
    @DisplayName("Should return all products")
    void getAllProducts_ShouldReturnProductList() {
        // Given
        Product anotherProduct = Product.builder()
                .id(2L)
                .name("Another Product")
                .price(BigDecimal.valueOf(49.99))
                .build();

        ProductDto anotherProductDto = ProductDto.builder()
                .id(2L)
                .name("Another Product")
                .price(BigDecimal.valueOf(49.99))
                .build();

        List<Product> products = Arrays.asList(testProduct, anotherProduct);
        List<ProductDto> expectedDtos = Arrays.asList(testProductDto, anotherProductDto);

        given(productRepository.findAll()).willReturn(products);
        given(productMapper.toDto(testProduct)).willReturn(testProductDto);
        given(productMapper.toDto(anotherProduct)).willReturn(anotherProductDto);

        // When
        List<ProductDto> result = productService.getAllProducts();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedDtos);

        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(2)).toDto(any(Product.class));
    }

    /**
     * Тест получения продукта по существующему ID.
     */
    @Test
    @DisplayName("Should return product when product exists")
    void getProductById_WithExistingId_ShouldReturnProduct() {
        // Given
        given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));
        given(productMapper.toDto(testProduct)).willReturn(testProductDto);

        // When
        ProductDto result = productService.getProductById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(99.99));

        verify(productRepository, times(1)).findById(1L);
        verify(productMapper, times(1)).toDto(testProduct);
    }

    /**
     * Тест получения продукта по несуществующему ID.
     */
    @Test
    @DisplayName("Should throw exception when product not found")
    void getProductById_WithNonExistingId_ShouldThrowException() {
        // Given
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");

        verify(productRepository, times(1)).findById(999L);
        verify(productMapper, never()).toDto(any());
    }

    /**
     * Тест создания нового продукта.
     */
    @Test
    @DisplayName("Should create and return product")
    void createProduct_ShouldCreateAndReturnProduct() {
        // Given
        ProductDto newProductDto = ProductDto.builder()
                .name("New Product")
                .description("New Description")
                .price(BigDecimal.valueOf(79.99))
                .quantity(15)
                .categoryId(1L)
                .build();

        Product newProduct = Product.builder()
                .name("New Product")
                .description("New Description")
                .price(BigDecimal.valueOf(79.99))
                .quantity(15)
                .build();

        Product savedProduct = Product.builder()
                .id(3L)
                .name("New Product")
                .description("New Description")
                .price(BigDecimal.valueOf(79.99))
                .quantity(15)
                .build();

        ProductDto savedProductDto = ProductDto.builder()
                .id(3L)
                .name("New Product")
                .description("New Description")
                .price(BigDecimal.valueOf(79.99))
                .quantity(15)
                .categoryId(1L)
                .build();

        given(productMapper.toEntity(newProductDto)).willReturn(newProduct);
        given(productRepository.save(newProduct)).willReturn(savedProduct);
        given(productMapper.toDto(savedProduct)).willReturn(savedProductDto);

        // When
        ProductDto result = productService.createProduct(newProductDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("New Product");

        verify(productMapper, times(1)).toEntity(newProductDto);
        verify(productRepository, times(1)).save(newProduct);
        verify(productMapper, times(1)).toDto(savedProduct);
    }

    /**
     * Тест обновления существующего продукта.
     */
    @Test
    @DisplayName("Should update and return product when product exists")
    void updateProduct_WithExistingProduct_ShouldUpdateAndReturnProduct() {
        // Given
        ProductDto updateDto = ProductDto.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(BigDecimal.valueOf(89.99))
                .quantity(20)
                .categoryId(2L)
                .build();

        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Updated Product")
                .description("Updated Description")
                .price(BigDecimal.valueOf(89.99))
                .quantity(20)
                .build();

        ProductDto updatedProductDto = ProductDto.builder()
                .id(1L)
                .name("Updated Product")
                .description("Updated Description")
                .price(BigDecimal.valueOf(89.99))
                .quantity(20)
                .categoryId(2L)
                .build();

        given(productRepository.findById(1L)).willReturn(Optional.of(testProduct));
        given(productRepository.save(any(Product.class))).willReturn(updatedProduct);
        given(productMapper.toDto(updatedProduct)).willReturn(updatedProductDto);

        // When
        ProductDto result = productService.updateProduct(1L, updateDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated Product");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(89.99));

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).toDto(updatedProduct);
    }

    /**
     * Тест удаления продукта.
     */
    @Test
    @DisplayName("Should delete product when product exists")
    void deleteProduct_WithExistingProduct_ShouldDeleteProduct() {
        // Given
        given(productRepository.existsById(1L)).willReturn(true);
        willDoNothing().given(productRepository).deleteById(1L);

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    /**
     * Тест удаления несуществующего продукта.
     */
    @Test
    @DisplayName("Should throw exception when deleting non-existing product")
    void deleteProduct_WithNonExistingProduct_ShouldThrowException() {
        // Given
        given(productRepository.existsById(999L)).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> productService.deleteProduct(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 999");

        verify(productRepository, times(1)).existsById(999L);
        verify(productRepository, never()).deleteById(anyLong());
    }

    /**
     * Тест получения продуктов по категории.
     */
    @Test
    @DisplayName("Should return products by category id")
    void getProductsByCategoryId_ShouldReturnProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        List<ProductDto> expectedDtos = Arrays.asList(testProductDto);

        given(productRepository.findByCategoryId(1L)).willReturn(products);
        given(productMapper.toDto(testProduct)).willReturn(testProductDto);

        // When
        List<ProductDto> result = productService.getProductsByCategoryId(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryId()).isEqualTo(1L);

        verify(productRepository, times(1)).findByCategoryId(1L);
        verify(productMapper, times(1)).toDto(testProduct);
    }

    /**
     * Тест поиска продуктов по имени.
     */
    @Test
    @DisplayName("Should return products by name containing search term")
    void searchProductsByName_ShouldReturnMatchingProducts() {
        // Given
        String searchTerm = "test";
        List<Product> products = Arrays.asList(testProduct);
        List<ProductDto> expectedDtos = Arrays.asList(testProductDto);

        given(productRepository.findByNameContainingIgnoreCase(searchTerm)).willReturn(products);
        given(productMapper.toDto(testProduct)).willReturn(testProductDto);

        // When
        List<ProductDto> result = productService.searchProductsByName(searchTerm);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).containsIgnoringCase(searchTerm);

        verify(productRepository, times(1)).findByNameContainingIgnoreCase(searchTerm);
        verify(productMapper, times(1)).toDto(testProduct);
    }
}
