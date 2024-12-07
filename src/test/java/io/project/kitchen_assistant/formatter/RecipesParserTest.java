package io.project.kitchen_assistant.formatter;

import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;

public class RecipesParserTest {

    private RecipesParser recipesParser;

    @BeforeEach
    public void setUp() {
        recipesParser = new RecipesParser();
    }

    @Test
    public void testParseRecipesTextWithValidInput() {
        String input = String.join("\n",
                "1. Pancakes",
                "Ингредиенты: мука, молоко; Яйцо.",
                "Приготовление: Смешать все.",
                "2. Omelette",
                "Ингредиенты: Яйцо, соль.",
                "Приготовление: Взбить яйца.");

        List<RecipeCreateDTO> recipes = recipesParser.parseRecipesText(input);

        assertEquals(2, recipes.size());
        assertEquals("Pancakes", recipes.getFirst().getName());
        assertEquals(List.of("мука", "молоко", "яйцо"), recipes.get(0).getIngredients());
        assertEquals("Смешать все.", recipes.get(0).getInstructions());

        assertEquals("Omelette", recipes.get(1).getName());
        assertEquals(List.of("яйцо", "соль"), recipes.get(1).getIngredients());
        assertEquals("Взбить яйца.", recipes.get(1).getInstructions());
    }

    @Test
    public void testParseRecipesTextWithEmptyInput() {
        String input = "";
        List<RecipeCreateDTO> recipes = recipesParser.parseRecipesText(input);

        assertTrue(recipes.isEmpty());
    }

    @Test
    public void testParseRecipesTextWithInvalidFormat() {
        String input = String.join("\n", "1. Pancakes", "Ингредиенты: мука");
        List<RecipeCreateDTO> recipes = recipesParser.parseRecipesText(input);

        assertTrue(recipes.isEmpty());
    }

    @Test
    public void testParseNameWithValidName() {
        String name = recipesParser.parseName("Pancakes**&^%");
        assertEquals("Pancakes", name);
    }

    @Test
    public void testParseNameWithEmptyInput() {
        String name = recipesParser.parseName("");
        assertEquals("", name);
    }

    @Test
    public void testParseIngredientsWithValidIngredients() {
        String ingredientsText = "мука, молоко; Яйцо.";
        List<String> ingredients = recipesParser.parseIngredients(ingredientsText);
        assertEquals(List.of("мука", "молоко", "яйцо"), ingredients);
    }

    @Test
    public void testParseIngredientsWithEmptyInput() {
        List<String> ingredients = recipesParser.parseIngredients("");
        assertTrue(ingredients.isEmpty());
    }

    @Test
    public void testFormatIngredient() {
        String formattedIngredient = recipesParser.formatIngredient("молоко;.");
        assertEquals("молоко", formattedIngredient);
    }
}
