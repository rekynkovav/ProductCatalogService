package com.productCatalogService.mapper;

import com.productCatalogService.dto.CategoryDTO;
import com.productCatalogService.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Интерфейс маппера для преобразования между сущностью Category и DTO объектами.
 * Использует MapStruct для автоматической генерации реализации.
 *
 * @apiNote Все методы генерируются автоматически MapStruct на основе аннотаций
 * @see Category
 * @see CategoryDTO
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Преобразует сущность Category в CategoryDTO.
     *
     * @param category исходная сущность категории
     * @return DTO объект категории
     */
    CategoryDTO toDTO(Category category);

    /**
     * Преобразует CategoryDTO в сущность Category.
     * Игнорирует поле id при преобразовании.
     *
     * @param categoryDTO DTO объект категории
     * @return сущность категории без установленного идентификатора
     */
    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryDTO categoryDTO);

    /**
     * Преобразует DTO для создания категории в сущность Category.
     * Игнорирует поле id при преобразовании.
     *
     * @param createCategory DTO с данными для создания категории
     * @return сущность категории без установленного идентификатора
     */
    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryDTO.CreateCategory createCategory);

    /**
     * Обновляет существующую сущность Category данными из DTO обновления.
     * Игнорирует поле id при обновлении.
     *
     * @param updateCategory DTO с данными для обновления категории
     * @param category       целевая сущность для обновления
     */
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(CategoryDTO.UpdateCategory updateCategory, @MappingTarget Category category);
}