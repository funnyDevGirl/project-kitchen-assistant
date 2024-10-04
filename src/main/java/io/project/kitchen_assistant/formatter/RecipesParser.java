package io.project.kitchen_assistant.formatter;

import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@AllArgsConstructor
public class RecipesParser {

    private static final Logger logger = LoggerFactory.getLogger(RecipesParser.class);

    public List<RecipeCreateDTO> parseRecipes(String input) {
        logger.info("The beginning of recipe parsing");

        List<RecipeCreateDTO> recipes = new ArrayList<>();

        Pattern recipePattern = Pattern.compile("(.+?)Ингредиенты:(.+?)Приготовление:(.+?)(?=\\d+\\. |\\z)", Pattern.DOTALL);

        Matcher matcher = recipePattern.matcher(input);

        while (matcher.find()) {
            String nameText = matcher.group(1).trim();
            String ingredientsText = matcher.group(2).trim();
            String instructionsText = matcher.group(3).trim();

            String name = parseName(nameText);
            logger.debug("Это \"Название\" блюда: \"{}\"", name);

            List<String> ingredients = parseIngredients(ingredientsText);
            logger.debug("Это \"Ингредиенты\": \"{}\"", ingredients);

            List<String> instructions = parseInstructions(instructionsText);
            logger.debug("Это \"Приготовление\": \"{}\"", instructions);

            recipes.add(new RecipeCreateDTO(name, ingredients, instructions));
        }
        return recipes;
    }

    private String parseName(String text) {
        return text.replaceAll("[\\d\\p{Punct}]", "").trim();
    }

    private List<String> parseIngredients(String text) {
        // делю строку по ". " или ";", но сохраняю разделитель ". ", если след. подстрока начинается с маленькой буквы
        return Arrays.stream(text.split("(?<=\\. )(?=[А-Я])|(?<=\\. )(?=[a-z])|(?<=; )"))
                .map(i -> i.replace(".", "").replace(";", "").trim())
                .map(i -> {
                    if (!i.isEmpty()) {
                        return i.substring(0, 1).toUpperCase() + i.substring(1);
                    }
                    return i;
                })
                .toList();
    }

    private List<String> parseInstructions(String text) {
        return Arrays.stream(text.split("\\. "))
                .map(i -> i.replace(".", "").trim())
                .map(i -> {
                    if (!i.isEmpty()) {
                        return i.substring(0, 1).toUpperCase() + i.substring(1);
                    }
                    return i;
                })
                .toList();
    }
}
