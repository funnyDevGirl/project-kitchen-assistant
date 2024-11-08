package io.project.kitchen_assistant.dto.recipes;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс для представления существующего рецепта.
 * Содержит идентификатор и информацию о рецепте.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDTO {
    private Long id;
    private String name;
    @Size(max = 255, message = "Ingredients must be 255 characters or less")
    private String ingredients;
    @Size(max = 1000, message = "Instructions must be 1000 characters or less")
    private String instructions;
}
