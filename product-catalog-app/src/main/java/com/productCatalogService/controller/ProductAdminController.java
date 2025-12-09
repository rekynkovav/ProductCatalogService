package com.productCatalogService.controller;

import com.productCatalogService.dto.ProductDTO;
import com.productCatalogService.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для административного управления товарами.
 */
@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ProductDTO.CreateProduct createProduct) {

        ProductDTO productDTO = productService.createProduct(token, createProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO.UpdateProduct updateProduct) {

        ProductDTO productDTO = productService.updateProduct(token, id, updateProduct);
        return ResponseEntity.ok(productDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        productService.deleteProduct(token, id);
        return ResponseEntity.noContent().build();
    }
}