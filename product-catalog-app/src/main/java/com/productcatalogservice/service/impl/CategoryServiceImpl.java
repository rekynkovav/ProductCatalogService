package com.productcatalogservice.service.impl;

import com.productcatalogservice.dto.CategoryDTO;
import com.productcatalogservice.dto.ProductDTO;
import com.productcatalogservice.entity.Category;
import com.productcatalogservice.entity.Product;
import com.productcatalogservice.entity.Role;
import com.productcatalogservice.entity.User;
import com.productcatalogservice.exception.AccessDeniedException;
import com.productcatalogservice.exception.ResourceNotFoundException;
import com.productcatalogservice.mapper.CategoryMapper;
import com.productcatalogservice.mapper.ProductMapper;
import com.productcatalogservice.repository.CategoryRepository;
import com.productcatalogservice.repository.ProductRepository;
import com.productcatalogservice.service.CategoryService;
import com.productcatalogservice.util.AuthUtil;
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
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AuthUtil authUtil;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public boolean deleteById(Long id) {
        return categoryRepository.deleteById(id);
    }

    @Override
    public List<Product> findProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = findAll();
        return categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категория", "id", id));
        return categoryMapper.toDTO(category);
    }

    @Override
    public List<ProductDTO> getProductsByCategoryIdDto(Long categoryId) {
        List<Product> products = findProductsByCategoryId(categoryId);
        return productMapper.toDTOList(products);
    }

    @Override
    public CategoryDTO createCategory(String token, CategoryDTO.CreateCategory createCategory) {
        checkAdminAccess(token);

        Category category = categoryMapper.toEntity(createCategory);
        Category savedCategory = save(category);

        log.info("Категория создана: {} (ID: {})", savedCategory.getName(), savedCategory.getId());
        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    public CategoryDTO updateCategory(String token, Long id, CategoryDTO.UpdateCategory updateCategory) {
        checkAdminAccess(token);

        Category existingCategory = findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категория", "id", id));

        categoryMapper.updateEntityFromDTO(updateCategory, existingCategory);
        Category updatedCategory = save(existingCategory);

        log.info("Категория обновлена: {} (ID: {})", updatedCategory.getName(), updatedCategory.getId());
        return categoryMapper.toDTO(updatedCategory);
    }

    @Override
    public Boolean deleteCategory(String token, Long id) {
        checkAdminAccess(token);

        if (!findById(id).isPresent()) {
            throw new ResourceNotFoundException("Категория", "id", id);
        }

        boolean isDeleted = deleteById(id); // Предполагаем, что deleteById() теперь возвращает boolean

        if (isDeleted) {
            log.info("Категория успешно удалена: ID: {}", id);
        } else {
            log.warn("Категория не была удалена: ID: {}", id);
        }

        return isDeleted;
    }

    private void checkAdminAccess(String token) {
        User user = authUtil.getUserByToken(token);
        if (user == null || !Role.ADMIN.equals(user.getRole())) {
            throw new AccessDeniedException("Доступ запрещен. Требуется роль ADMIN");
        }
    }
}
