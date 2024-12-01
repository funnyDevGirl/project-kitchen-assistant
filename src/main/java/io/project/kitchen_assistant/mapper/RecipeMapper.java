package io.project.kitchen_assistant.mapper;

import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import io.project.kitchen_assistant.dto.recipes.RecipeDTO;
import io.project.kitchen_assistant.model.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class RecipeMapper {

    public abstract Recipe map(RecipeCreateDTO recipeCreateDTO);
    public abstract RecipeDTO map(Recipe recipe);
}
