package org.example.testContainers;

import org.example.model.entity.Product;
import org.example.servlet.BasketResponse;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса BasketResponse
 */
class BasketResponseTest {

    @Test
    void testDefaultConstructor() {
        BasketResponse response = new BasketResponse();

        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getBasketItems());
        assertEquals(0.0, response.getTotalAmount());
    }

    @Test
    void testConstructorWithSuccessAndMessage() {
        BasketResponse response = new BasketResponse(true, "Success");

        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertNull(response.getBasketItems());
        assertEquals(0.0, response.getTotalAmount());
    }

    @Test
    void testFullConstructor() {
        Map<Long, Product> basketItems = Map.of(
                1L, new Product("Product1", 2, 100, null),
                2L, new Product("Product2", 1, 200, null)
        );
        double totalAmount = 400.0;

        BasketResponse response = new BasketResponse(true, "Basket loaded", basketItems, totalAmount);

        assertTrue(response.isSuccess());
        assertEquals("Basket loaded", response.getMessage());
        assertEquals(basketItems, response.getBasketItems());
        assertEquals(totalAmount, response.getTotalAmount());
    }

    @Test
    void testSetters() {
        BasketResponse response = new BasketResponse();
        Map<Long, Product> basketItems = Map.of(1L, new Product("Test", 1, 50, null));

        response.setSuccess(true);
        response.setMessage("Test message");
        response.setBasketItems(basketItems);
        response.setTotalAmount(150.0);

        assertTrue(response.isSuccess());
        assertEquals("Test message", response.getMessage());
        assertEquals(basketItems, response.getBasketItems());
        assertEquals(150.0, response.getTotalAmount());
    }
}