package org.example.mapper;

import org.example.dto.CategoryDTO;
import org.example.model.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDTO(Category category);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryDTO categoryDTO);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryDTO.CreateCategory createCategory);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(CategoryDTO.UpdateCategory updateCategory, @MappingTarget Category category);

    List<CategoryDTO> toDTOList(List<Category> categories);
}