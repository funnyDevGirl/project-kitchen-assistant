package io.project.kitchen_assistant.service.impl;

import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import io.project.kitchen_assistant.dto.recipes.RecipeDTO;
import io.project.kitchen_assistant.dto.recipes.gpt.GptResponse;
import io.project.kitchen_assistant.exception.RecipeNotFoundException;
import io.project.kitchen_assistant.formatter.Formatter;
import io.project.kitchen_assistant.formatter.RecipesParser;
import io.project.kitchen_assistant.mapper.RecipeMapper;
import io.project.kitchen_assistant.model.Recipe;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.RecipeRepository;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.service.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import static io.project.kitchen_assistant.utils.FileReader.readResourceFile;
import static java.lang.String.format;

@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {

    private final AppConfig appConfig;
    private final RecipesParser recipesParser;
    private final RecipeMapper recipeMapper;
    private final RestTemplate restTemplateGptApi;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public RecipeServiceImpl(AppConfig appConfig, RecipesParser recipesParser, RecipeMapper recipeMapper,
                             @Qualifier("restTemplateForGpt") RestTemplate restTemplateGptApi,
                             RecipeRepository recipeRepository, UserRepository userRepository) {
        this.appConfig = appConfig;
        this.recipesParser = recipesParser;
        this.recipeMapper = recipeMapper;
        this.restTemplateGptApi = restTemplateGptApi;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    public RecipeDTO create(RecipeCreateDTO recipeCreateDTO, String userEmail) {
        Recipe recipe = recipeMapper.map(recipeCreateDTO);

        User currentUser = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new UsernameNotFoundException(format("User with email '%s' is not logged in or does not exist", userEmail)));

        recipe.setUser(currentUser);
        Recipe savedRecipe = recipeRepository.save(recipe);

        return recipeMapper.map(savedRecipe);
    }

    public List<RecipeDTO> getAllRecipesByUserEmail(String email) {
        List<Recipe> recipes = recipeRepository.findByUserEmail(email);
        log.info("{} recipes were Received from the DB", recipes.size());

        return recipes.stream()
                .map(recipeMapper::map)
                .toList();
    }

    public RecipeDTO findById(long id) {
        var recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException(format("Recipe with id: '%s' not found", id)));

        return recipeMapper.map(recipe);
    }

    public void delete(long id) {
        recipeRepository.deleteById(id);
        log.info("Recipe with id '{}' successfully deleted", id);
    }

    public List<RecipeCreateDTO> searchRecipes(String query) throws Exception {

        String requestBody = createRequestBody(query);
        log.debug("Constructed HTTP request body:\n{}", requestBody);

        try {
            log.info("Starting an API request");

            ResponseEntity<GptResponse> response = restTemplateGptApi.exchange(appConfig.getGptApiUrl(), HttpMethod.POST, new HttpEntity<>(requestBody), GptResponse.class);
            log.debug("Response from GPT: '{}'", response);

            if (response.getBody() != null && response.getBody().getResult() != null &&
                    response.getBody().getResult().getAlternatives() != null) {

                String textWithRecipes = response.getBody().getResult().getAlternatives().getFirst().getMessage().getText();
                log.debug("Text with recipes: '{}'", textWithRecipes);

                String recipesTextWithoutMD = Formatter.formatMarkdownToText(textWithRecipes);
                log.debug("Recipes Text Without MD: '{}'", recipesTextWithoutMD);

                List<RecipeCreateDTO> recipes = recipesParser.parseRecipesText(recipesTextWithoutMD);
                log.info("{} recipes have been successfully received from the API", recipes.size());

                return recipes;

            } else {
                return List.of();
            }

        } catch (HttpClientErrorException e) {
            log.error("Error when getting the recipe list", e);
        }
        log.info("Request completed successfully");

        return List.of();
    }

    private String createRequestBody(String query) throws Exception {
        String frameForRequestToGpt = readResourceFile("frameForRequestToGpt.json");
        return frameForRequestToGpt.replace("%s", query).replace("\"\"", "\"");
    }
}
