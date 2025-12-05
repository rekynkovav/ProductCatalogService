package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.model.entity.Product;
import org.example.repository.ProductRepository;
import org.example.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

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
}