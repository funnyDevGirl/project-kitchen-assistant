package io.project.kitchen_assistant.dto.recipes;

import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
public class RecipeDTO {
    private Long id;
    private String name;
    private List<String> ingredients; // String ?
    private List<String> cookingInstructions; // String ?
}
