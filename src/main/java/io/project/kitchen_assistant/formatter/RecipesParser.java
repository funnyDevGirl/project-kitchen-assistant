package io.project.kitchen_assistant.formatter;

import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@AllArgsConstructor
public class RecipesParser {

    // парсит рецепт на строки
    public List<RecipeCreateDTO> parseRecipesText(String input) {
        log.info("The beginning of recipe parsing");

        List<RecipeCreateDTO> recipes = new ArrayList<>();

        Pattern recipePattern = Pattern.compile("(.+?)Ингредиенты:(.+?)Приготовление:(.+?)(?=\\d+\\. |\\z)", Pattern.DOTALL);
        Matcher matcher = recipePattern.matcher(input);

        while (matcher.find()) {
            String nameText = matcher.group(1).trim();
            String ingredientsText = matcher.group(2).trim();
            String instructionsText = matcher.group(3).trim();

            String name = parseName(nameText);
            log.info("Это Название блюда: '{}'", name);
            log.info("Это Ингредиенты: '{}'", ingredientsText);
            log.info("Это Приготовление: '{}'", instructionsText);

            recipes.add(new RecipeCreateDTO(name, ingredientsText, instructionsText));
        }
        return recipes;
    }

    private String parseName(String text) {
        return text.replaceAll("[\\d\\p{Punct}]", "").trim();
    }
}
