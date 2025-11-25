package org.example.testContainers;

import org.example.model.entity.Category;
import org.example.model.entity.Product;
import org.example.repository.impl.ProductRepositoryImpl;
import org.example.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ProductServiceImplTest extends BaseDatabaseTest {

    private ProductServiceImpl productService = ProductServiceImpl.getInstance();
    private ProductRepositoryImpl productRepository = ProductRepositoryImpl.getInstance();

    @BeforeEach
    void setUp() {
    }

    @Test
    void testSaveProduct() {
        // Given
        Product product = createTestProduct("Test Product", 10, 100, Category.ELECTRONICS);

        // When
        productService.saveProduct(product);

        // Then
        Optional<Product> foundProduct = productRepository.findAll().stream()
                .filter(p -> p.getName().equals("Test Product"))
                .findFirst();
        assertThat(foundProduct).isPresent();
    }

    @Test
    void testUpdateProduct() {
        // Given
        Product product = createTestProduct("Old Name", 5, 50, Category.OTHER);
        Product savedProduct = productRepository.save(product);

        // When
        productService.updateProduct(savedProduct.getId(), "New Name", 10, 100, Category.ELECTRONICS);

        // Then
        Optional<Product> updatedProduct = productService.findById(savedProduct.getId());
        assertThat(updatedProduct).isPresent();
        assertThat(updatedProduct.get().getName()).isEqualTo("New Name");
        assertThat(updatedProduct.get().getPrice()).isEqualTo(100);
    }

    @Test
    void testDeleteProductById() {
        // Given
        Product product = createTestProduct("To Delete", 5, 100, Category.OTHER);
        Product savedProduct = productRepository.save(product);

        // When
        productService.deleteProductById(savedProduct.getId());

        // Then
        Optional<Product> deletedProduct = productService.findById(savedProduct.getId());
        assertThat(deletedProduct).isEmpty();
    }

    @Test
    void testFindById() {
        // Given
        Product product = createTestProduct("Find Me", 5, 100, Category.BOOKS);
        Product savedProduct = productRepository.save(product);

        // When
        Optional<Product> foundProduct = productService.findById(savedProduct.getId());

        // Then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Find Me");
    }

    private Product createTestProduct(String name, int quantity, int price, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setQuantity(quantity);
        product.setPrice(price);
        product.setCategory(category);
        return product;
    }
}