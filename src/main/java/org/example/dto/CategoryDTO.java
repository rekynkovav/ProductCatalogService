package org.example.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object (DTO) для категории товаров.
 * Используется для передачи данных между слоями приложения и валидации входных данных.
 *
 * <p>Содержит вложенные классы для операций создания и обновления категории.</p>
 *
 * @see CategoryDTO.CreateCategory
 * @see CategoryDTO.UpdateCategory
 */
@Data
public class CategoryDTO {

    /** Уникальный идентификатор категории. Автоматически генерируется БД. */
    private Long id;

    /** Название категории. Обязательное поле. */
    @NotBlank(message = "Название категории обязательно")
    private String name;

    /**
     * DTO для операции создания категории.
     * Не содержит поля id, так как оно генерируется автоматически.
     */
    @Data
    public static class CreateCategory {

        /** Название категории. Обязательное поле. */
        @NotBlank(message = "Название категории обязательно")
        private String name;
    }

    /**
     * DTO для операции обновления категории.
     * Не содержит поля id, так как оно передается в URL.
     */
    @Data
    public static class UpdateCategory {

        /** Название категории. Обязательное поле. */
        @NotBlank(message = "Название категории обязательно")
        private String name;
    }
}