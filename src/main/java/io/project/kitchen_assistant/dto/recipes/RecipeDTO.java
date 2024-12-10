package io.project.kitchen_assistant.dto.recipes;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * Класс для представления существующего рецепта.
 * Содержит идентификатор и информацию о рецепте.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "RecipeDTO's information")
public class RecipeDTO {

    @Schema(description = "Recipe ID", example = "12", type = "long")
    private Long id;

    @Schema(description = "Recipe's name", example = "Вишневый пирог", type = "string")
    private String name;

    @ArraySchema(
            schema = @Schema(description = "Ingredients in the recipe", example = "Вишня - 500г", type = "string"),
            arraySchema = @Schema(description = "List with Recipe's ingredients",
                    example = "[\"Вишня - 500г\", \"Тесто - 1кг\"]")
    )
    private List<String> ingredients;

    @Schema(description = "String with Recipe's instructions",
            example = "Раскатать тесто, выложить вишню. Запекать 40мин при 180гр.", type = "string")
    private String instructions;
}
