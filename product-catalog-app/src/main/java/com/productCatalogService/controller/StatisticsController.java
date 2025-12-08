package com.productCatalogService.controller;

import com.productCatalogService.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Контроллер для получения статистики.
 */
@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStatistics(@RequestHeader("Authorization") String token) {
        Map<String, Object> statistics = statisticsService.getStatistics(token);
        return ResponseEntity.ok(statistics);
    }
}