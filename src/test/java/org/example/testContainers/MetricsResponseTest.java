package org.example.testContainers;

import org.example.servlet.MetricsResponse;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для класса MetricsResponse
 */
class MetricsResponseTest {

    @Test
    void testConstructorAndGetters() {
        Map<String, Integer> metrics = Map.of(
                "LOGIN_COUNT", 10,
                "PRODUCT_ADD_COUNT", 5
        );

        MetricsResponse response = new MetricsResponse(true, "Success", metrics);

        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(metrics, response.getSystemMetrics());
    }

    @Test
    void testWithEmptyMetrics() {
        Map<String, Integer> emptyMetrics = Map.of();

        MetricsResponse response = new MetricsResponse(false, "Error", emptyMetrics);

        assertFalse(response.isSuccess());
        assertEquals("Error", response.getMessage());
        assertThat(response.getSystemMetrics()).isEmpty();
    }
}