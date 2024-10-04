package io.project.kitchen_assistant.service;

import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import io.project.kitchen_assistant.dto.recipes.RecipeDTO;
import io.project.kitchen_assistant.exception.ResourceNotFoundException;
import io.project.kitchen_assistant.mapper.RecipeMapper;
import io.project.kitchen_assistant.formatter.Formatter;
import io.project.kitchen_assistant.formatter.RecipesParser;
import io.project.kitchen_assistant.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RecipeService {

    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    @Value("${api-url}")
    private String apiUrl;

    @Value("${FOLDER_ID}")
    private String id;

    @Value("${IAM_TOKEN}")
    private String token;

    private final RecipesParser recipesParser;
    private final RestTemplate restTemplate;
    private final RecipeMapper recipeMapper;
    private final RecipeRepository recipeRepository;


    public RecipeDTO create(RecipeCreateDTO recipeCreateDTO) {
        var recipe = recipeMapper.map(recipeCreateDTO);
        recipeRepository.save(recipe);

        return recipeMapper.map(recipe);
    }

    public List<RecipeDTO> getAll() {
        var recipe = recipeRepository.findAll();

        return recipe.stream()
                .map(recipeMapper::map)
                .toList();
    }

    public RecipeDTO findById(Long id) {
        var recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe With Id: " + id + " Not Found"));

        return recipeMapper.map(recipe);
    }

    public void delete(Long id) throws Exception {
        recipeRepository.deleteById(id);
    }

    public List<RecipeCreateDTO> searchRecipes(String query) {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON); // Установка заголовков
        headers.set("x-folder-id", id);
        headers.set("Authorization", token);

        HttpEntity<String> entity = getStringHttpEntity(query, headers); // Создание тела запроса

        try {
            logger.info("Starting an API request");

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            String recipesResponseText = extractText(response.getBody()); // фрагент с рецептами

            System.out.println("Нужный кусок текста с рецептами: \"" + recipesResponseText + "\"");

            // удаляю Markdown с помощью jsoup, также * и \n
            String newRecipesResponseText = Formatter.formatMarkdownToText(recipesResponseText);

            System.out.println("А сейчас удален MD, все * и \\n:  " + "\"" + newRecipesResponseText + "\"");

            var recipes = recipesParser.parseRecipes(newRecipesResponseText);

            logger.info("{} recipes have been successfully received from the API", recipes.size());

            // ---------------------------------------------- УДАЛИТЬ ПОСЛЕ ВСЕХ ПРОВЕРОК
            System.out.println("Debug after parsing starting!");

            int count = 1;
            for (RecipeCreateDTO recipe : recipes) {

                System.out.printf("Рецепт №: %s%n", count);
                System.out.printf("Название: \"%s\"%n", recipe.getName());

                System.out.println("Ингредиенты:");
                for (String ingredient : recipe.getIngredients()) {
                    System.out.printf("-> \"%s\"%n", ingredient);
                }

                System.out.println("Приготовление:");
                for (String step : recipe.getCookingInstructions()) {
                    System.out.printf("# \"%s\"%n", step);
                }
                System.out.println();
                count++;
            }
            // ---------------------------------------------- УДАЛИТЬ ПОСЛЕ ВСЕХ ПРОВЕРОК

            return recipes;

        } catch (HttpClientErrorException e) {
            logger.error("Error when getting the recipe list", e);
        }
        logger.info("Request completed successfully");

        return List.of();
    }

    private static HttpEntity<String> getStringHttpEntity(String userInput, HttpHeaders headers) {
        String requestBody = """
                {
                    "modelUri": "gpt://b1g9e9k3q9k3fptu8ka4/yandexgpt-lite",
                    "completionOptions": {
                        "stream": false,
                        "temperature": 0.1,
                        "maxTokens": "1000"
                    },
                    "messages": [
                        {
                            "role": "system",
                            "text": "Ответ нужно вывести в следующем шаблоне: 1) Название блюда\\n2) Ингредиенты\\n3) Приготовление.\\nНайди любые 3 рецепта, которые могут мне подойти, с ингредиентами:"
                        },
                        {
                            "role": "user",
                            "text": %s
                        }
                    ]
                }
                """.formatted(userInput);
        return new HttpEntity<>(requestBody, headers);
    }

    private String extractText(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse); // Парсинг JSON

        return jsonObject.getJSONObject("result") // Извлечение текста с рецептами
                .getJSONArray("alternatives")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("text");
    }
}
