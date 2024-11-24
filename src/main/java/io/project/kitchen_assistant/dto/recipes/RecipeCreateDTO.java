package io.project.kitchen_assistant.dto.recipes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Класс, представляющий собой DTO (Data Transfer Object) для создания рецептов.
 * Не содержит идентификатор, так как рецепт новый и еще не хранится в системе.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreateDTO {
    private String name;
    private List<String> ingredients;
    private String instructions;
}
