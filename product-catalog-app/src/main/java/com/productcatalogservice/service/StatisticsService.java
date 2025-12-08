package com.productcatalogservice.service;

import com.productcatalogservice.exception.AccessDeniedException;

import java.util.Map;

/**
 * Сервис для работы со статистикой системы
 */
public interface StatisticsService {

    /**
     * Получение статистики системы (только для администраторов)
     */
    Map<String, Object> getStatistics(String token) throws AccessDeniedException;
}