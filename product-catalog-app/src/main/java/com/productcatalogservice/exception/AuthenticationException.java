package com.productcatalogservice.exception;

/**
 * Исключение, выбрасываемое при ошибках аутентификации.
 * Используется вместо BadCredentialsException из Spring Security.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}