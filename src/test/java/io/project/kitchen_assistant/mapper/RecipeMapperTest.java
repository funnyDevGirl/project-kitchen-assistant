package io.project.kitchen_assistant.mapper;

import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import io.project.kitchen_assistant.dto.recipes.RecipeDTO;
import io.project.kitchen_assistant.model.Recipe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
public class RecipeMapperTest {

    @InjectMocks
    private RecipeMapperImpl recipeMapper;

    @Test
    void testMapRecipeCreateDTOToRecipe() {
        RecipeCreateDTO recipeCreateDTO = new RecipeCreateDTO();
        recipeCreateDTO.setName("Пицца");
        recipeCreateDTO.setIngredients(Arrays.asList("тесто", "томаты", "сыр"));
        recipeCreateDTO.setInstructions("На тесто выложите томаты, посыпьте сыром и выпекайте при 180 градусов 30мин.");

        Recipe recipe = recipeMapper.map(recipeCreateDTO);

        assertThat(recipe).isNotNull();
        assertThat(recipe.getName()).isEqualTo(recipeCreateDTO.getName());
        assertThat(recipe.getIngredients()).isEqualTo(recipeCreateDTO.getIngredients());
        assertThat(recipe.getInstructions()).isEqualTo(recipeCreateDTO.getInstructions());
    }

    @Test
    void testMapRecipeToRecipeDTO() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Пицца");
        recipe.setIngredients(Arrays.asList("тесто", "томаты", "сыр"));
        recipe.setInstructions("На тесто выложите томаты, посыпьте сыром и выпекайте при 180 градусов 30мин.");

        RecipeDTO recipeDTO = recipeMapper.map(recipe);

        assertThat(recipeDTO).isNotNull();
        assertThat(recipeDTO.getId()).isEqualTo(recipe.getId());
        assertThat(recipeDTO.getName()).isEqualTo(recipe.getName());
        assertThat(recipeDTO.getIngredients()).isEqualTo(recipe.getIngredients());
    }
}
