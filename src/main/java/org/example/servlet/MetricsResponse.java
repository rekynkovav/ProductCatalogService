package org.example.servlet;

import java.util.Map;
/**
 * DTO класс для ответа с метриками системы.
 * Содержит информацию об успешности операции, сообщение и карту метрик системы.
 * Используется для передачи данных о производительности и статистике клиенту.
 */
public class MetricsResponse {
    private boolean success;
    private String message;
    private Map<String, Integer> systemMetrics;

    public MetricsResponse(boolean success, String message,
                           Map<String, Integer> systemMetrics) {
        this.success = success;
        this.message = message;
        this.systemMetrics = systemMetrics;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Integer> getSystemMetrics() {
        return systemMetrics;
    }
}

