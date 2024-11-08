package io.project.kitchen_assistant.dto.recipes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс, представляющий собой DTO (Data Transfer Object) для создания рецептов.
 * Не содержит идентификатор, так как рецепт новый и еще не хранится в системе.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreateDTO {
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 255, message = "Ingredients must be 255 characters or less")
    private String ingredients;
    @NotBlank
    @Size(max = 1000, message = "Instructions must be 1000 characters or less")
    private String instructions;
}
