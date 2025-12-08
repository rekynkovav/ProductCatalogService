package com.productcatalogservice.controller;

import com.productcatalogservice.dto.CategoryDTO;
import com.productcatalogservice.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Контроллер для административного управления категориями.
 * Предоставляет REST API для создания, обновления и удаления категорий.
 * Все операции требуют наличия валидного токена авторизации в заголовке запроса.
 */
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategoryService categoryService;

    /**
     * Создает новую категорию.
     *
     * @param token Токен авторизации, переданный в заголовке запроса.
     * @param createCategory DTO с данными для создания новой категории.
     * @return ResponseEntity с созданной категорией (статус 201) или сообщением об ошибке (статус 403).
     */
    @PostMapping
    public ResponseEntity<?> createCategory(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CategoryDTO.CreateCategory createCategory) {

        try {
            CategoryDTO categoryDTO = categoryService.createCategory(token, createCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body(categoryDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Обновляет существующую категорию по идентификатору.
     *
     * @param token Токен авторизации, переданный в заголовке запроса.
     * @param id Идентификатор категории для обновления.
     * @param updateCategory DTO с данными для обновления категории.
     * @return ResponseEntity с обновленной категорией или сообщением об ошибке.
     *         Возвращает статус 404, если категория не найдена.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO.UpdateCategory updateCategory) {

        try {
            CategoryDTO categoryDTO = categoryService.updateCategory(token, id, updateCategory);
            return ResponseEntity.ok(categoryDTO);
        } catch (Exception e) {
            if (e.getMessage().contains("не найден")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Удаляет категорию по идентификатору.
     *
     * @param token Токен авторизации, переданный в заголовке запроса.
     * @param id Идентификатор категории для удаления.
     * @return ResponseEntity без содержимого (статус 204) или сообщением об ошибке.
     *         Возвращает статус 404, если категория не найдена.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        try {
            categoryService.deleteCategory(token, id);
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