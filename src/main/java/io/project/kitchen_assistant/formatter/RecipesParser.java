package io.project.kitchen_assistant.formatter;

import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class RecipesParser {

    public List<RecipeCreateDTO> parseRecipesText(String input) {
        log.info("The beginning of recipe parsing");

        List<RecipeCreateDTO> recipes = new ArrayList<>();

        input = input.trim();

        int startIndex = 0;
        int recipeCount = 1;

        while (true) {
            int recipeIndex = input.indexOf(recipeCount + ". ", startIndex);
            if (recipeIndex == -1) {
                break;
            }

            int nextRecipeIndex = input.indexOf(recipeCount + 1 + ". ", recipeIndex + 1);

            String recipeEntry;
            if (nextRecipeIndex == -1) {
                recipeEntry = input.substring(recipeIndex);
            } else {
                recipeEntry = input.substring(recipeIndex, nextRecipeIndex);
            }

            String[] parts = recipeEntry.split("Ингредиенты:|Приготовление:");

            if (parts.length < 3) {
                log.warn("The recipe entry is not in the expected format: {}", recipeEntry);
                startIndex = recipeIndex + 1;
                continue;
            }

            String nameText = parts[0].trim();
            String ingredientsText = parts[1].trim();
            String instructionsText = parts[2].trim();

            String name = parseName(nameText);
            log.debug("Name of the dish: '{}'", name);
            log.debug("Ingredients: '{}'", ingredientsText);
            log.debug("Instructions: '{}'", instructionsText);

            recipes.add(new RecipeCreateDTO(name, parseIngredients(ingredientsText), instructionsText));

            startIndex = recipeIndex + 1;
            recipeCount++;
        }

        return recipes;
    }

    private String parseName(String text) {
        StringBuilder nameBuilder = new StringBuilder();

        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch) || Character.isWhitespace(ch)) {
                nameBuilder.append(ch);
            }
        }
        return nameBuilder.toString().trim();
    }

    private List<String> parseIngredients(String text) {
        List<String> ingredients = new ArrayList<>();

        text = text.trim();
        int lastIndex = 0;

        while (lastIndex < text.length()) {

            int dotIndex = text.indexOf(", ", lastIndex);
            int semicolonIndex = text.indexOf("; ", lastIndex);

            int nextIndex;
            if (dotIndex != -1 && (semicolonIndex == -1 || dotIndex < semicolonIndex)) {
                nextIndex = dotIndex;
            } else if (semicolonIndex != -1) {
                nextIndex = semicolonIndex;
            } else {
                ingredients.add(formatIngredient(text.substring(lastIndex).trim()));
                break;
            }

            String ingredient = text.substring(lastIndex, nextIndex);
            ingredients.add(formatIngredient(ingredient.trim()));

            lastIndex = nextIndex + 1;
        }

        return ingredients;
    }

    private String formatIngredient(String ingredient) {
        ingredient = ingredient.replace(".", "")
                .replace(";", "")
                .trim();
        return ingredient.toLowerCase();
    }
}
