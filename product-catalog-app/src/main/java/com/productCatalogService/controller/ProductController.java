package com.productCatalogService.controller;

import com.productCatalogService.dto.ProductDTO;
import com.productCatalogService.dto.ProductPageDTO;
import com.productCatalogService.service.CategoryService;
import com.productCatalogService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с товарами.
 * Предоставляет REST API для получения информации о товарах.
 * Доступен всем пользователям без авторизации.
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    /**
     * Получает список товаров с пагинацией.
     *
     * @param page Номер страницы (по умолчанию 0).
     * @param size Количество элементов на странице (по умолчанию 20).
     * @return ResponseEntity с DTO страницы товаров.
     */
    @GetMapping
    public ResponseEntity<ProductPageDTO> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        ProductPageDTO productPageDTO = productService.getPaginatedProducts(page, size);
        return ResponseEntity.ok(productPageDTO);
    }

    /**
     * Получает товар по его идентификатору.
     *
     * @param id Идентификатор товара.
     * @return ResponseEntity с DTO товара.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO productDTO = productService.getProductById(id);
        return ResponseEntity.ok(productDTO);
    }

    /**
     * Получает список товаров по идентификатору категории.
     *
     * @param categoryId Идентификатор категории.
     * @return ResponseEntity со списком товаров в указанной категории.
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategoryId(@PathVariable Long categoryId) {
        List<ProductDTO> products = categoryService.getProductsByCategoryIdDto(categoryId);
        return ResponseEntity.ok(products);
    }
}