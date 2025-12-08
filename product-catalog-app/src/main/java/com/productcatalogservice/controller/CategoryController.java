package com.productcatalogservice.controller;

import com.productcatalogservice.dto.CategoryDTO;
import com.productcatalogservice.dto.ProductDTO;
import com.productcatalogservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с категориями.
 * Предоставляет REST API для получения информации о категориях.
 * Доступен всем пользователям без авторизации.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Получает список всех категорий.
     *
     * @return ResponseEntity со списком всех категорий.
     */
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Получает категорию по ее идентификатору.
     *
     * @param id Идентификатор категории.
     * @return ResponseEntity с DTO категории.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO categoryDTO = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryDTO);
    }

    /**
     * Получает список товаров в указанной категории.
     *
     * @param id Идентификатор категории.
     * @return ResponseEntity со списком товаров в указанной категории.
     */
    @GetMapping("/{id}/products")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Long id) {
        List<ProductDTO> products = categoryService.getProductsByCategoryIdDto(id);
        return ResponseEntity.ok(products);
    }
}