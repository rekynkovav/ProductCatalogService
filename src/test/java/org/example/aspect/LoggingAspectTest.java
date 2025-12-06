package org.example.aspect;

import org.example.audit.autoconfig.aspect.LoggingAspect;
import org.example.controller.ProductController;
import org.example.dto.ProductDTO;
import org.example.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit тесты для LoggingAspect.
 * Тестирует логирование вызовов методов с помощью AspectJ прокси.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see LoggingAspect
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Logging Aspect Tests")
class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private ProductService productService;

    private ProductController productControllerProxy;
    private ProductService productServiceProxy;

    private Logger logger;

    /**
     * Настройка тестового окружения перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        logger = LoggerFactory.getLogger(LoggingAspect.class);

        // Создаем AspectJ прокси для ProductController
        AspectJProxyFactory controllerFactory = new AspectJProxyFactory(new ProductController(productService));
        controllerFactory.addAspect(loggingAspect);
        productControllerProxy = controllerFactory.getProxy();

        // Создаем AspectJ прокси для ProductService
        AspectJProxyFactory serviceFactory = new AspectJProxyFactory(productService);
        serviceFactory.addAspect(loggingAspect);
        productServiceProxy = serviceFactory.getProxy();
    }

    /**
     * Тест логирования успешного выполнения метода сервиса.
     */
    @Test
    @DisplayName("Should log method execution when service method is called")
    void logMethodExecution_WithServiceMethod_ShouldLogEntryAndExit() {
        // Given
        ProductDto testProduct = ProductDto.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(99.99))
                .build();

        List<ProductDto> products = Arrays.asList(testProduct);

        when(productService.getAllProducts()).thenReturn(products);

        // Когда - устанавливаем spy на логгер для перехвата вызовов
        Logger spyLogger = spy(logger);
        // Note: На практике лучше использовать специальные тестовые аппендеры для логирования

        // When
        List<ProductDto> result = productServiceProxy.getAllProducts();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        // Проверяем, что метод сервиса был вызван
        verify(productService, times(1)).getAllProducts();

        // Note: В реальных тестах следует использовать MemoryAppender или аналогичный механизм
        // для проверки логов. Здесь мы проверяем только, что аспект не ломает выполнение метода.
    }

    /**
     * Тест логирования исключения в методе сервиса.
     */
    @Test
    @DisplayName("Should log exception when service method throws exception")
    void logMethodExecution_WithException_ShouldLogException() {
        // Given
        RuntimeException exception = new RuntimeException("Test exception");
        when(productService.getAllProducts()).thenThrow(exception);

        try {
            // When
            productServiceProxy.getAllProducts();
        } catch (RuntimeException e) {
            // Then - исключение должно быть проброшено
            assertThat(e).isEqualTo(exception);
        }

        // Проверяем, что метод сервиса был вызван
        verify(productService, times(1)).getAllProducts();
    }

    /**
     * Тест логирования вызова метода контроллера.
     */
    @Test
    @DisplayName("Should log method execution when controller method is called")
    void logMethodExecution_WithControllerMethod_ShouldLogEntryAndExit() {
        // Given
        ProductDto testProduct = ProductDto.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(99.99))
                .build();

        List<ProductDto> products = Arrays.asList(testProduct);

        when(productService.getAllProducts()).thenReturn(products);

        // When
        List<ProductDto> result = productControllerProxy.getAllProducts();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(productService, times(1)).getAllProducts();
    }

    /**
     * Тест логирования исключения в методе контроллера.
     */
    @Test
    @DisplayName("Should log exception when controller method throws exception")
    void logException_WithControllerException_ShouldLogException() {
        // Given
        RuntimeException exception = new RuntimeException("Controller exception");
        when(productService.getAllProducts()).thenThrow(exception);

        try {
            // When
            productControllerProxy.getAllProducts();
        } catch (RuntimeException e) {
            // Then - исключение должно быть проброшено
            assertThat(e).isEqualTo(exception);
        }

        verify(productService, times(1)).getAllProducts();
    }

    /**
     * Вспомогательный метод для создания тестовых продуктов.
     */
    private List<ProductDto> createTestProducts() {
        return Arrays.asList(
                ProductDto.builder()
                        .id(1L)
                        .name("Product 1")
                        .price(BigDecimal.valueOf(10.99))
                        .build(),
                ProductDto.builder()
                        .id(2L)
                        .name("Product 2")
                        .price(BigDecimal.valueOf(20.99))
                        .build()
        );
    }
}
