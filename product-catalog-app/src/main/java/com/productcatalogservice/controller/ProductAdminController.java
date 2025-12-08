package com.productcatalogservice.controller;

import com.productcatalogservice.dto.ProductDTO;
import com.productcatalogservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Контроллер для административного управления товарами.
 * Предоставляет REST API для создания, обновления и удаления товаров.
 * Все операции требуют наличия валидного токена авторизации в заголовке запроса.
 */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductService productService;

    /**
     * Создает новый товар.
     *
     * @param token Токен авторизации, переданный в заголовке запроса.
     * @param createProduct DTO с данными для создания нового товара.
     * @return ResponseEntity с созданным товаром (статус 201) или сообщением об ошибке (статус 403).
     */
    @PostMapping
    public ResponseEntity<?> createProduct(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ProductDTO.CreateProduct createProduct) {

        try {
            ProductDTO productDTO = productService.createProduct(token, createProduct);
            return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Обновляет существующий товар по идентификатору.
     *
     * @param token Токен авторизации, переданный в заголовке запроса.
     * @param id Идентификатор товара для обновления.
     * @param updateProduct DTO с данными для обновления товара.
     * @return ResponseEntity с обновленным товаром или сообщением об ошибке.
     *         Возвращает статус 404, если товар не найден.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO.UpdateProduct updateProduct) {

        try {
            ProductDTO productDTO = productService.updateProduct(token, id, updateProduct);
            return ResponseEntity.ok(productDTO);
        } catch (Exception e) {
            if (e.getMessage().contains("не найден")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Удаляет товар по идентификатору.
     *
     * @param token Токен авторизации, переданный в заголовке запроса.
     * @param id Идентификатор товара для удаления.
     * @return ResponseEntity без содержимого (статус 204) или сообщением об ошибке.
     *         Возвращает статус 404, если товар не найден.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        try {
            productService.deleteProduct(token, id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            if (e.getMessage().contains("не найден")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}