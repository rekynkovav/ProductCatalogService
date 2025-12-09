package com.productCatalogService.service.impl;

import com.productCatalogService.dto.ProductDTO;
import com.productCatalogService.dto.ProductPageDTO;
import com.productCatalogService.entity.Category;
import com.productCatalogService.entity.Product;
import com.productCatalogService.entity.Role;
import com.productCatalogService.entity.User;
import com.productCatalogService.exception.AccessDeniedException;
import com.productCatalogService.exception.ResourceNotFoundException;
import com.productCatalogService.mapper.ProductMapper;
import com.productCatalogService.repository.CategoryRepository;
import com.productCatalogService.repository.ProductRepository;
import com.productCatalogService.service.ProductService;
import com.productCatalogService.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final AuthUtil authUtil;
    private final CategoryRepository categoryRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public boolean deleteById(Long id) {
        return productRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public boolean decreaseQuantity(Long productId, int quantity) {
        return productRepository.decreaseQuantity(productId, quantity);
    }

    @Override
    public boolean increaseQuantity(Long productId, int quantity) {
        return productRepository.increaseQuantity(productId, quantity);
    }

    @Override
    public List<Product> findAllPaginated(int page, int size) {
        return productRepository.findAllPaginated(page, size);
    }

    @Override
    public Long count() {
        return productRepository.count();
    }

    @Override
    public ProductPageDTO getPaginatedProducts(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0 || size > 100) {
            size = 20;
        }

        List<Product> products = findAllPaginated(page, size);
        long totalProducts = count();
        long totalPages = (long) Math.ceil((double) totalProducts / size);

        ProductPageDTO dto = new ProductPageDTO();
        dto.setProducts(productMapper.toDTOList(products));
        dto.setPage(page);
        dto.setSize(size);
        dto.setTotalProducts(totalProducts);
        dto.setTotalPages(totalPages);
        dto.setHasNext(page < totalPages - 1);
        dto.setHasPrevious(page > 0);

        return dto;
    }
    @Override
    public ProductDTO getProductById(Long id) {
        Product product = findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Товар", "id", id));
        return productMapper.toDTO(product);
    }

    @Override
    public List<ProductDTO> getProductsByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found with id: " + categoryId);
        }

        List<Product> products = productRepository.findByCategoryId(categoryId);

        return productMapper.toDTOList(products);
    }

    @Override
    public ProductDTO createProduct(String token, ProductDTO.CreateProduct createProduct) {
        checkAdminAccess(token);

        Product product = productMapper.toEntity(createProduct);
        Product savedProduct = save(product);

        log.info("Товар создан: {} (ID: {})", savedProduct.getName(), savedProduct.getId());
        return productMapper.toDTO(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(String token, Long id, ProductDTO.UpdateProduct updateProduct) {
        checkAdminAccess(token);

        Product existingProduct = findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Товар", "id", id));

        productMapper.updateEntityFromDTO(updateProduct, existingProduct);
        existingProduct.setId(id);

        Product updatedProduct = save(existingProduct);

        log.info("Товар обновлен: {} (ID: {})", updatedProduct.getName(), updatedProduct.getId());
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(String token, Long id) {
        checkAdminAccess(token);

        if (!existsById(id)) {
            throw new ResourceNotFoundException("Товар", "id", id);
        }

        deleteById(id);
        log.info("Товар удален: ID: {}", id);
    }

    private void checkAdminAccess(String token) {
        User user = authUtil.getUserByToken(token);
        if (user == null || !Role.ADMIN.equals(user.getRole())) {
            throw new AccessDeniedException("Доступ запрещен. Требуется роль ADMIN");
        }
    }
}