package io.project.kitchen_assistant.service;

import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import io.project.kitchen_assistant.dto.recipes.RecipeDTO;
import java.util.List;

public interface RecipeService {

    RecipeDTO create(RecipeCreateDTO dto, String userName);

    List<RecipeDTO> getAllRecipesByUserEmail(String email);

    RecipeDTO findById(long id);

    void delete(long id);

    List<RecipeCreateDTO> searchRecipes(String query) throws Exception;
}
