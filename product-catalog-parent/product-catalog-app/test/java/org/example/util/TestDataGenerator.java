import com.productcatalogservice.dto.CategoryDto;
import com.productcatalogservice.dto.ProductDto;
import com.productcatalogservice.dto.TagDto;
import com.productcatalogservice.model.Category;
import com.productcatalogservice.model.Product;
import com.productcatalogservice.model.Tag;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Утилитный класс для генерации тестовых данных.
 * Предоставляет методы создания тестовых объектов для всех сущностей приложения.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 */
public class TestDataGenerator {

    private TestDataGenerator() {
        // Utility class - запрет создания экземпляров
    }

    /**
     * Создает тестовый продукт (сущность).
     *
     * @param id идентификатор продукта (может быть null для нового продукта)
     * @return сгенерированный продукт
     */
    public static Product createTestProduct(Long id) {
        return Product.builder()
                .id(id)
                .name("Test Product " + (id != null ? id : ""))
                .description("Test Description for Product " + (id != null ? id : ""))
                .price(BigDecimal.valueOf(99.99))
                .quantity(10)
                .categoryId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Создает тестовый ProductDto.
     *
     * @param id идентификатор продукта (может быть null для нового продукта)
     * @return сгенерированный ProductDto
     */
    public static ProductDto createTestProductDto(Long id) {
        return ProductDto.builder()
                .id(id)
                .name("Test Product DTO " + (id != null ? id : ""))
                .description("Test Description for Product DTO " + (id != null ? id : ""))
                .price(BigDecimal.valueOf(99.99))
                .quantity(10)
                .categoryId(1L)
                .build();
    }

    /**
     * Создает список тестовых продуктов.
     *
     * @param count количество продуктов в списке
     * @return список сгенерированных продуктов
     */
    public static List<Product> createTestProducts(int count) {
        return Arrays.stream(new Product[count])
                .map((product, index) -> createTestProduct((long) (index + 1)))
                .toList();
    }

    /**
     * Создает список тестовых ProductDto.
     *
     * @param count количество ProductDto в списке
     * @return список сгенерированных ProductDto
     */
    public static List<ProductDto> createTestProductDtos(int count) {
        return Arrays.stream(new ProductDto[count])
                .map((productDto, index) -> createTestProductDto((long) (index + 1)))
                .toList();
    }

    /**
     * Создает тестовую категорию (сущность).
     *
     * @param id идентификатор категории (может быть null для новой категории)
     * @return сгенерированная категория
     */
    public static Category createTestCategory(Long id) {
        return Category.builder()
                .id(id)
                .name("Test Category " + (id != null ? id : ""))
                .description("Test Description for Category " + (id != null ? id : ""))
                .parentId(id != null && id > 1 ? id - 1 : null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Создает тестовый CategoryDto.
     *
     * @param id идентификатор категории (может быть null для новой категории)
     * @return сгенерированный CategoryDto
     */
    public static CategoryDto createTestCategoryDto(Long id) {
        return CategoryDto.builder()
                .id(id)
                .name("Test Category DTO " + (id != null ? id : ""))
                .description("Test Description for Category DTO " + (id != null ? id : ""))
                .parentId(id != null && id > 1 ? id - 1 : null)
                .build();
    }

    /**
     * Создает тестовый тег (сущность).
     *
     * @param id идентификатор тега (может быть null для нового тега)
     * @return сгенерированный тег
     */
    public static Tag createTestTag(Long id) {
        return Tag.builder()
                .id(id)
                .name("Test Tag " + (id != null ? id : ""))
                .description("Test Description for Tag " + (id != null ? id : ""))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Создает тестовый TagDto.
     *
     * @param id идентификатор тега (может быть null для нового тега)
     * @return сгенерированный TagDto
     */
    public static TagDto createTestTagDto(Long id) {
        return TagDto.builder()
                .id(id)
                .name("Test Tag DTO " + (id != null ? id : ""))
                .description("Test Description for Tag DTO " + (id != null ? id : ""))
                .build();
    }

    /**
     * Создает продукт с минимальными обязательными полями.
     *
     * @return продукт с минимальными данными
     */
    public static Product createMinimalProduct() {
        return Product.builder()
                .name("Minimal Product")
                .price(BigDecimal.valueOf(1.00))
                .quantity(1)
                .categoryId(1L)
                .build();
    }

    /**
     * Создает ProductDto с минимальными обязательными полями.
     *
     * @return ProductDto с минимальными данными
     */
    public static ProductDto createMinimalProductDto() {
        return ProductDto.builder()
                .name("Minimal Product DTO")
                .price(BigDecimal.valueOf(1.00))
                .quantity(1)
                .categoryId(1L)
                .build();
    }

    /**
     * Создает невалидный продукт для тестирования валидации.
     *
     * @return невалидный продукт
     */
    public static Product createInvalidProduct() {
        return Product.builder()
                .name("")  // Пустое имя
                .price(BigDecimal.valueOf(-10.00))  // Отрицательная цена
                .quantity(-5)  // Отрицательное количество
                .build();
    }

    /**
     * Создает невалидный ProductDto для тестирования валидации.
     *
     * @return невалидный ProductDto
     */
    public static ProductDto createInvalidProductDto() {
        return ProductDto.builder()
                .name("")  // Пустое имя
                .price(BigDecimal.valueOf(-10.00))  // Отрицательная цена
                .quantity(-5)  // Отрицательное количество
                .build();
    }
}