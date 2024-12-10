package io.project.kitchen_assistant.dto.recipes;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO for create Recipe")
public class RecipeCreateDTO {

    @Schema(description = "Recipe's name", example = "Вишневый пирог", type = "string")
    @NotBlank
    private String name;

    @ArraySchema(
            schema = @Schema(description = "Ingredients in the recipe", example = "Вишня - 500г", type = "string"),
            arraySchema = @Schema(description = "List with Recipe's ingredients",
                    example = "[\"Вишня - 500г\", \"Тесто - 1кг\"]")
    )
    @NotEmpty(message = "Ingredients must not be empty")
    private List<String> ingredients;

    @Schema(description = "String with Recipe's instructions",
            example = "Раскатать тесто, выложить вишню. Запекать 40мин при 180гр.", type = "string")
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
