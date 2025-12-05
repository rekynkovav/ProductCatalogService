package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для REST контроллеров.
 * Обрабатывает исключения валидации и возвращает структурированные сообщения об ошибках.
 *
 * @apiNote Этот класс перехватывает исключения на уровне всех контроллеров
 * @see ControllerAdvice
 * @see ExceptionHandler
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключения валидации входных параметров.
     * Возвращает карту с именами полей и сообщениями об ошибках.
     *
     * @param ex исключение MethodArgumentNotValidException, содержащее ошибки валидации
     * @return ResponseEntity с картой ошибок и статусом HTTP 400 (BAD_REQUEST)
     * @apiNote Формат ответа: {"fieldName": "error message", ...}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}