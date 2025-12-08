package com.productCatalogService.exception;

/**
 * Исключение, выбрасываемое при конфликте данных (например, дублирование уникальных полей).
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}