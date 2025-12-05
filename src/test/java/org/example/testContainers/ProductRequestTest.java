package org.example.testContainers;

import org.example.servlet.ProductRequest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для класса ProductRequest
 */
class ProductRequestTest {

    @Test
    void testDefaultConstructor() {
        ProductRequest request = new ProductRequest();

        assertNull(request.getName());
        assertEquals(0, request.getQuantity());
        assertEquals(0, request.getPrice());
        assertNull(request.getCategory());
        assertNull(request.getId());
    }

    @Test
    void testParameterizedConstructor() {
        String name = "Test Product";
        int quantity = 10;
        int price = 100;
        String category = "ELECTRONICS";

        ProductRequest request = new ProductRequest(name, quantity, price, category);

        assertEquals(name, request.getName());
        assertEquals(quantity, request.getQuantity());
        assertEquals(price, request.getPrice());
        assertEquals(category, request.getCategory());
        assertNull(request.getId());
    }

    @Test
    void testSettersAndGetters() {
        ProductRequest request = new ProductRequest();
        Long id = 1L;
        String name = "New Product";
        int quantity = 5;
        int price = 50;
        String category = "BOOKS";

        request.setId(id);
        request.setName(name);
        request.setQuantity(quantity);
        request.setPrice(price);
        request.setCategory(category);

        assertEquals(id, request.getId());
        assertEquals(name, request.getName());
        assertEquals(quantity, request.getQuantity());
        assertEquals(price, request.getPrice());
        assertEquals(category, request.getCategory());
    }

    @Test
    void testToString() {
        ProductRequest request = new ProductRequest("Test", 5, 100, "CATEGORY");
        request.setId(1L);

        String toString = request.toString();

        assertThat(toString)
                .contains("Test")
                .contains("5")
                .contains("100")
                .contains("CATEGORY")
                .contains("1");
    }
}