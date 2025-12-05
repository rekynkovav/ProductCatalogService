package org.example.testContainers;

import org.example.servlet.BasketRequest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса BasketRequest
 */
class BasketRequestTest {

    @Test
    void testDefaultConstructor() {
        BasketRequest request = new BasketRequest();

        assertNull(request.getProductId());
        assertEquals(0, request.getQuantity());
    }

    @Test
    void testParameterizedConstructor() {
        Long productId = 123L;
        int quantity = 5;

        BasketRequest request = new BasketRequest(productId, quantity);

        assertEquals(productId, request.getProductId());
        assertEquals(quantity, request.getQuantity());
    }

    @Test
    void testSettersAndGetters() {
        BasketRequest request = new BasketRequest();
        Long productId = 456L;
        int quantity = 10;

        request.setProductId(productId);
        request.setQuantity(quantity);

        assertEquals(productId, request.getProductId());
        assertEquals(quantity, request.getQuantity());
    }

    @Test
    void testToString() {
        BasketRequest request = new BasketRequest(789L, 3);
        String toString = request.toString();

        assertTrue(toString.contains("789"));
        assertTrue(toString.contains("3"));
        assertTrue(toString.contains("BasketRequest"));
    }
}