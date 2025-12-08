package com.productcatalogservice.mapper;

import com.productcatalogservice.dto.ProductDTO;
import com.productcatalogservice.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Интерфейс маппера для преобразования между сущностью Product и DTO объектами.
 * Использует MapStruct для автоматической генерации реализации.
 *
 * @apiNote Все методы генерируются автоматически MapStruct на основе аннотаций
 * @see Product
 * @see ProductDTO
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Преобразует сущность Product в ProductDTO.
     *
     * @param product исходная сущность товара
     * @return DTO объект товара
     */
    ProductDTO toDTO(Product product);

    /**
     * Преобразует ProductDTO в сущность Product.
     * Игнорирует поле id при преобразовании.
     *
     * @param productDTO DTO объект товара
     * @return сущность товара без установленного идентификатора
     */
    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDTO productDTO);

    /**
     * Преобразует DTO для создания товара в сущность Product.
     * Игнорирует поле id при преобразовании.
     *
     * @param createProduct DTO с данными для создания товара
     * @return сущность товара без установленного идентификатора
     */
    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDTO.CreateProduct createProduct);

    /**
     * Обновляет существующую сущность Product данными из DTO обновления.
     * Игнорирует поле id при обновлении.
     *
     * @param updateProduct DTO с данными для обновления товара
     * @param product       целевая сущность для обновления
     */
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(ProductDTO.UpdateProduct updateProduct, @MappingTarget Product product);

    /**
     * Преобразует список сущностей Product в список ProductDTO.
     *
     * @param products список сущностей товаров
     * @return список DTO объектов товаров
     */
    List<ProductDTO> toDTOList(List<Product> products);
}