package controller;

import com.productCatalogService.controller.StatisticsController;
import com.productCatalogService.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StatisticsControllerTest {

    @Mock
    private StatisticsService statisticsService;

    @InjectMocks
    private StatisticsController statisticsController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(statisticsController).build();
    }

    @Test
    void getStatistics_WithValidToken_ShouldReturnStatistics() throws Exception {
        // Arrange
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalUsers", 100);
        statistics.put("totalProducts", 500);
        statistics.put("totalCategories", 10);
        statistics.put("activeOrders", 25);

        when(statisticsService.getStatistics("Bearer admin-token")).thenReturn(statistics);

        // Act & Assert
        mockMvc.perform(get("/api/admin/statistics")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(100))
                .andExpect(jsonPath("$.totalProducts").value(500))
                .andExpect(jsonPath("$.totalCategories").value(10))
                .andExpect(jsonPath("$.activeOrders").value(25));

        verify(statisticsService, times(1)).getStatistics("Bearer admin-token");
    }
}
