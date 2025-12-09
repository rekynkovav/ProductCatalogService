package com.productCatalogService.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ProductPageDTO {
    private List<ProductDTO> products;
    private int page;
    private int size;
    private long totalProducts;
    private long totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}