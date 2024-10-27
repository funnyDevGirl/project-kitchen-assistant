package io.project.kitchen_assistant.dto.recipes;

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
    private String ingredients;
    private String instructions;
}
