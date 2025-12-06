package org.example.controller;

import org.example.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsControllerTest {

    @Mock
    private StatisticsService statisticsService;

    private StatisticsController statisticsController;

    @BeforeEach
    void setUp() {
        statisticsController = new StatisticsController(statisticsService);
    }

    @Test
    void testGetStatistics_Success() {
        String token = "Bearer admintoken";
        Map<String, Object> stats = Map.of(
                "totalUsers", 100,
                "totalProducts", 500,
                "totalCategories", 10,
                "activeSessions", 25
        );

        when(statisticsService.getStatistics(token)).thenReturn(stats);

        ResponseEntity<?> response = statisticsController.getStatistics(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(100, responseBody.get("totalUsers"));
        assertEquals(500, responseBody.get("totalProducts"));
        assertEquals(10, responseBody.get("totalCategories"));
        assertEquals(25, responseBody.get("activeSessions"));
    }

    @Test
    void testGetStatistics_AccessDenied() {
        String token = "Bearer usertoken";

        when(statisticsService.getStatistics(token))
                .thenThrow(new SecurityException("Доступ запрещен"));

        ResponseEntity<?> response = statisticsController.getStatistics(token);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Доступ запрещен", ((Map<?, ?>) response.getBody()).get("error"));
    }
}