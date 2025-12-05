package org.example.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDTO {
    private Long id;

    @NotBlank(message = "Название категории обязательно")
    private String name;

    @Data
    public static class CreateCategory {
        @NotBlank(message = "Название категории обязательно")
        private String name;
    }

    @Data
    public static class UpdateCategory {
        @NotBlank(message = "Название категории обязательно")
        private String name;
    }
}