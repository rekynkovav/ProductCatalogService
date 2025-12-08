package com.productcatalogservice.controller;

import com.productcatalogservice.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Контроллер для получения статистики.
 * Предоставляет REST API для получения статистической информации системы.
 * Все операции требуют наличия валидного токена авторизации в заголовке запроса.
 */
@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Получает статистические данные системы.
     *
     * @param token Токен авторизации, переданный в заголовке запроса.
     * @return ResponseEntity со статистическими данными или сообщением об ошибке (статус 403).
     */
    @GetMapping
    public ResponseEntity<?> getStatistics(@RequestHeader("Authorization") String token) {
        try {
            Map<String, Object> statistics = statisticsService.getStatistics(token);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}