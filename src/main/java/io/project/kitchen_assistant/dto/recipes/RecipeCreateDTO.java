package io.project.kitchen_assistant.dto.recipes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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
    @NotBlank
    private String name;

    @NotEmpty(message = "Ingredients must not be empty")
    private List<String> ingredients;

    @Size(max = 1000, message = "Instructions must be 1000 characters or less")
    private String instructions;

    @Override
    public String toString() {
        return "RecipeCreateDTO{"
                + "name='" + name + '\''
                + ", ingredients=" + ingredients
                + ", instructions='" + instructions + '\''
                + '}';
    }
}
