package org.example.testContainers;

import org.example.servlet.ApiResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса ApiResponse
 */
class ApiResponseTest {

    @Test
    void testSuccessWithData() {
        String testData = "test data";
        ApiResponse<String> response = ApiResponse.success(testData);

        assertTrue(response.isSuccess());
        assertEquals("Operation completed successfully", response.getMessage());
        assertEquals(testData, response.getData());
        assertNull(response.getError());
    }

    @Test
    void testSuccessWithMessageAndData() {
        String message = "Custom message";
        Integer data = 42;
        ApiResponse<Integer> response = ApiResponse.success(message, data);

        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertNull(response.getError());
    }

    @Test
    void testError() {
        String errorMessage = "Error occurred";
        ApiResponse<String> response = ApiResponse.error(errorMessage);

        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getData());
        assertNull(response.getError());
    }

    @Test
    void testSetters() {
        ApiResponse<String> response = new ApiResponse<>();

        response.setSuccess(true);
        response.setMessage("Test message");
        response.setData("Test data");
        response.setError("Test error");

        assertTrue(response.isSuccess());
        assertEquals("Test message", response.getMessage());
        assertEquals("Test data", response.getData());
        assertEquals("Test error", response.getError());
    }

    @Test
    void testConstructorWithAllParameters() {
        ApiResponse<String> response = new ApiResponse<>(true, "Success", "Data");

        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals("Data", response.getData());
    }
}