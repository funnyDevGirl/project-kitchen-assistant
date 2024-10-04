package io.project.kitchen_assistant.dto.recipes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeCreateDTO {
    private String name;
    private List<String> ingredients; // String ?
    private List<String> cookingInstructions; // String ?
}
