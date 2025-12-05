package org.example.mapper;

import org.example.dto.ProductDTO;
import org.example.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toDTO(Product product);

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDTO productDTO);

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDTO.CreateProduct createProduct);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(ProductDTO.UpdateProduct updateProduct, @MappingTarget Product product);

    List<ProductDTO> toDTOList(List<Product> products);
}