package org.example.exception;

/**
 * Исключение, выбрасываемое при отказе в доступе.
 * Используется для обработки ошибок авторизации.
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}