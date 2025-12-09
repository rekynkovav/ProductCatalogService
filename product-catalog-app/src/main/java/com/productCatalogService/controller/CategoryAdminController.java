package com.productCatalogService.controller;

import com.productCatalogService.dto.CategoryDTO;
import com.productCatalogService.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для административного управления категориями.
 */
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CategoryDTO.CreateCategory createCategory) {

        CategoryDTO categoryDTO = categoryService.createCategory(token, createCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO.UpdateCategory updateCategory) {

        CategoryDTO categoryDTO = categoryService.updateCategory(token, id, updateCategory);
        return ResponseEntity.ok(categoryDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        categoryService.deleteCategory(token, id);
        return ResponseEntity.noContent().build();
    }
}