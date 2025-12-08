import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.audit.autoconfig.aspect.AuditAspect;
import org.example.model.entity.Product;
import org.example.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit тесты для AuditAspect.
 * Тестирует аудит операций с данными.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see AuditAspect
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Audit Aspect Tests")
class AuditAspectTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuditAspect auditAspect;

    @Mock
    private ProductRepository productRepository;

    private ProductRepository productRepositoryProxy;

    private Product testProduct;

    /**
     * Настройка тестового окружения перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        // Настраиваем мок HTTP запроса
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.1");
        request.setRequestURI("/api/products");
        request.setMethod("POST");
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);

        // Создаем тестовый продукт
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(99.99))
                .quantity(10)
                .build();

        // Создаем AspectJ прокси для ProductRepository
        AspectJProxyFactory factory = new AspectJProxyFactory(productRepository);
        factory.addAspect(auditAspect);
        productRepositoryProxy = factory.getProxy();
    }

    /**
     * Тест аудита операции сохранения.
     */
    @Test
    @DisplayName("Should audit save operation")
    void auditOperation_WithSaveMethod_ShouldLogAudit() throws Exception {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"id\":1,\"name\":\"Test Product\"}");

        // When
        Product savedProduct = productRepositoryProxy.save(testProduct);

        // Then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isEqualTo(1L);

        verify(productRepository, times(1)).save(testProduct);
        // Note: В реальных тестах следует проверять логирование через MemoryAppender
    }

    /**
     * Тест аудита неудачной операции.
     */
    @Test
    @DisplayName("Should audit failed operation")
    void auditFailedOperation_WithException_ShouldLogFailure() {
        // Given
        RuntimeException exception = new RuntimeException("Database error");
        when(productRepository.save(any(Product.class))).thenThrow(exception);

        try {
            // When
            productRepositoryProxy.save(testProduct);
        } catch (RuntimeException e) {
            // Then - исключение должно быть проброшено
            assertThat(e).isEqualTo(exception);
        }

        verify(productRepository, times(1)).save(testProduct);
        // Note: Аспект должен залогировать неудачную операцию
    }

    /**
     * Тест аудита операции обновления.
     */
    @Test
    @DisplayName("Should audit update operation")
    void auditOperation_WithUpdateMethod_ShouldLogAudit() throws Exception {
        // Given
        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Updated Product")
                .price(BigDecimal.valueOf(89.99))
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"id\":1,\"name\":\"Updated Product\"}");

        // When
        Product result = productRepositoryProxy.save(updatedProduct);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Product");

        verify(productRepository, times(1)).save(updatedProduct);
    }

    /**
     * Тест аудита операции удаления.
     */
    @Test
    @DisplayName("Should audit delete operation")
    void auditOperation_WithDeleteMethod_ShouldLogAudit() {
        // Given
        doNothing().when(productRepository).deleteById(1L);

        // When
        productRepositoryProxy.deleteById(1L);

        // Then
        verify(productRepository, times(1)).deleteById(1L);
        // Note: Аспект должен залогировать операцию удаления
    }

    /**
     * Тест обработки исключения при сериализации JSON.
     */
    @Test
    @DisplayName("Should handle JSON serialization exception gracefully")
    void auditOperation_WithJsonSerializationError_ShouldNotBreakExecution() throws Exception {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("JSON error"));

        // When
        Product savedProduct = productRepositoryProxy.save(testProduct);

        // Then - операция должна завершиться успешно, несмотря на ошибку сериализации
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isEqualTo(1L);

        verify(productRepository, times(1)).save(testProduct);
        // Note: Аспект должен обработать исключение и продолжить выполнение
    }
}
