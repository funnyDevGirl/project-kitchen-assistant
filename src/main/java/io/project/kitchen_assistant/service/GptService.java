package io.project.kitchen_assistant.service;

import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import io.project.kitchen_assistant.dto.recipes.gpt.GptResponse;
import io.project.kitchen_assistant.formatter.Formatter;
import io.project.kitchen_assistant.formatter.RecipesParser;
import io.project.kitchen_assistant.mapper.RecipeMapper;
import io.project.kitchen_assistant.repository.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static io.project.kitchen_assistant.utils.FileReader.readResourceFile;

// public class RecipeServiceImpl implements RecipeService {
@Slf4j
public class GptService {

    private final AppConfig appConfig;
    private final RecipesParser recipesParser;
    private final RestTemplate restTemplateGptApi;
    private final RecipeMapper recipeMapper;
    private final RecipeRepository recipeRepository;

    public GptService(AppConfig appConfig, RecipesParser recipesParser,
                             @Qualifier("restTemplateForGpt") RestTemplate restTemplateGptApi,
                             RecipeMapper recipeMapper, RecipeRepository recipeRepository) {
        this.appConfig = appConfig;
        this.recipesParser = recipesParser;
        this.restTemplateGptApi = restTemplateGptApi;
        this.recipeMapper = recipeMapper;
        this.recipeRepository = recipeRepository;
    }


//    public List<RecipeCreateDTO> searchRecipes(String query) throws Exception {
//
//        String requestBody = createRequestBody(query);
//        log.info("Constructed HTTP request body:\n{}", requestBody);
//
//        try {
//            log.info("Starting an API request");
//
//            ResponseEntity<GptResponse> response = restTemplateGptApi.exchange(appConfig.getGptApiUrl(), HttpMethod.POST, new HttpEntity<>(requestBody), GptResponse.class);
//            log.info("Response from GPT: '{}'", response);
//
//            log.info("GetBody: '{}'", response.getBody().getResult().getAlternatives().getFirst().getMessage().getText());
//            String extractedContentWithRecipes = recipesParser.getRecipeText(response.getBody().getResult().toString()); // фрагмент с рецептами
//
//            List<RecipeCreateDTO> recipes = recipesParser.parseRecipesText(
//                    Formatter.formatMarkdownToText(extractedContentWithRecipes));
//            log.info("{} recipes have been successfully received from the API", recipes.size());
//
//            return recipes;
//
//        } catch (HttpClientErrorException e) {
//            log.error("Error when getting the recipe list", e);
//        }
//        log.info("Request completed successfully");
//
//        return List.of();
//    }
//
//    private String createRequestBody(String query) throws Exception {
//        String frameForRequestToGpt = readResourceFile("frameForRequestToGpt.json");
//        return frameForRequestToGpt.replace("%s", query).replace("\"\"", "\"");
//    }
//}

//@Slf4j
//@Component
//@AllArgsConstructor
//public class RecipesParser {
//
//    private final ObjectMapper objectMapper;
//
//    // String response = "<200 OK OK,{\"result\":{\"alternatives\":[{\"message\":{\"role\":\"assistant\",\"text\":\"**1. Яблочная шарлотка**\\n\\n**Ингредиенты:**\\n* яблоки — 4–5 шт.;\\n* яйца — 4 шт.;\\n* сахар — 1 стакан;\\n* мука — 1 стакан;\\n* корица — по вкусу.\\n\\n**Приготовление:**\\n1. Яблоки очистить от сердцевины и нарезать кубиками или дольками.\\n2. Яйца взбить с сахаром до образования пены.\\n3. Добавить муку и корицу, перемешать.\\n4. Форму для выпечки смазать маслом или застелить пергаментом.\\n5. Выложить яблоки на дно формы.\\n6. Залить тестом.\\n7. Выпекать в разогретой до 180 градусов духовке 40–45 минут.\\n\\n**2. Яблочная запеканка с корицей**\\n\\n**Ингредиенты:**\\n* яблоки — 600 г;\\n* творог — 500 г;\\n* яйца — 2 шт.;\\n* сахар — 3 ст. ложки;\\n* манная крупа — 2 ст. ложки;\\n* сметана — 3 ст. ложки;\\n* корица — по вкусу.\\n\\n**Приготовление:**\\n1. Яблоки очистить и нарезать кубиками.\\n2. Творог размять вилкой.\\n3. Яйца взбить с сахаром.\\n4. Добавить творог, манную крупу и сметану, перемешать.\\n5. Форму для запекания смазать маслом или застелить пергаментом.\\n6. Выложить половину яблок на дно формы.\\n7. Залить творожной массой.\\n8. Сверху выложить оставшиеся яблоки.\\n9. Посыпать корицей.\\n10. Выпекать в разогретой до 180 градусов духовке 40–45 минут.\\n\\n**3. Яблочный пирог с корицей из слоёного теста**\\n\\n**Ингредиенты:**\\n* яблоки — 500 г;\\n* слоёное тесто — 500 г;\\n* сахар — 2 ст. ложки;\\n* корица — 1 ч. ложка;\\n* яйцо — 1 шт.;\\n* сахарная пудра — по вкусу.\\n\\n**Приготовление:**\\n1. Яблоки очистить и нарезать тонкими ломтиками.\\n2. Тесто раскатать и выложить в форму для выпечки.\\n3. Края теста смазать взбитым яйцом.\\n4. Яблоки выложить на тесто, посыпать сахаром и корицей.\\n5. Запекать в разогретой до 180 градусов духовке 30–35 минут.\\n6. Готовый пирог посыпать сахарной пудрой.\"},\"status\":\"ALTERNATIVE_STATUS_FINAL\"}],\"usage\":{\"inputTextTokens\":\"56\",\"completionTokens\":\"535\",\"totalTokens\":\"591\"},\"modelVersion\":\"22.05.2024\"}}>";
//
//    // возможно обработку исключений стоит перенести в сервис?
//    public String getRecipeText(String apiFullResponse) {
//        try {
//            GptResponse apiResponse = parseGptResponse(extractJson(apiFullResponse));
//
//            if (apiResponse.getResult() != null &&
//                    apiResponse.getResult().getAlternatives() != null) {
//
//                String contentWithRecipes = apiResponse.getResult().getAlternatives().getFirst().getMessage().getText();
//                log.info("Content with recipes: '{}'", contentWithRecipes);
//
//                return contentWithRecipes;
//            } else {
//                return "No recipes found";
//            }
//        } catch (IOException e) {
//            log.error("Error parsing JSON response");
//        }
//        return ""; // ???
//    }
//
//    private String extractJson(String apiFullResponse) {
//        int startIndex = apiFullResponse.indexOf('{');
//        int endIndex = apiFullResponse.lastIndexOf('}');
//
//        String jsonString = apiFullResponse.substring(startIndex, endIndex + 1);
//
//        log.info("The necessary part of the JSON with the recipe text: '{}'", jsonString);
//
//        return jsonString;
//    }
//
//    private GptResponse parseGptResponse(String json) throws IOException {
//        return objectMapper.readValue(json, GptResponse.class);
//    }
//
//    public List<RecipeCreateDTO> parseRecipesText(String input) {
//        log.info("The beginning of recipe parsing");
//
//        List<RecipeCreateDTO> recipes = new ArrayList<>();
//
//        Pattern recipePattern = Pattern.compile("(.+?)Ингредиенты:(.+?)Приготовление:(.+?)(?=\\d+\\. |\\z)", Pattern.DOTALL);
//
//        Matcher matcher = recipePattern.matcher(input);
//
//        while (matcher.find()) {
//            String nameText = matcher.group(1).trim();
//            String ingredientsText = matcher.group(2).trim();
//            String instructionsText = matcher.group(3).trim();
//
////            String name = parseName(nameText);
////            log.info("Это \"Название\" блюда: \"{}\"", name);
////
////            List<String> ingredients = parseIngredients(ingredientsText);
////            log.info("Это \"Ингредиенты\": \"{}\"", ingredients);
////
////            List<String> instructions = parseInstructions(instructionsText);
////            log.info("Это \"Приготовление\": \"{}\"", instructions);
////
////            recipes.add(new RecipeCreateDTO(name, ingredients, instructions));
//            recipes.add(new RecipeCreateDTO(nameText, ingredientsText, instructionsText));
//        }
//        return recipes;
//    }
//
//    private String parseName(String text) {
//        return text.replaceAll("[\\d\\p{Punct}]", "").trim();
//    }
//
//    private List<String> parseIngredients(String text) {
//        // делю строку по ". " или ";", но сохраняю разделитель ". ", если след. подстрока начинается с маленькой буквы
//        return Arrays.stream(text.split("(?<=\\. )(?=[А-Я])|(?<=\\. )(?=[a-z])|(?<=; )"))
//                .map(i -> i.replace(".", "").replace(";", "").trim())
//                .map(i -> {
//                    if (!i.isEmpty()) {
//                        return i.substring(0, 1).toUpperCase() + i.substring(1);
//                    }
//                    return i;
//                })
//                .toList();
//    }
//
////    private List<Ingredient> formIngredients(List<String> parsedIngredients) {
////        // Регулярное выражение для разбора строки с игредиентом:
////        Pattern pattern = Pattern.compile("(.*?) —\\s*(\\d+)\\s*([^;]+);");
////
////        List<Ingredient> ingredients = new ArrayList<>();
////
////        for (String ingredient : parsedIngredients) {
////            log.info("Preparation of the ingredient begins: '{}'", ingredient);
////
////            Matcher matcher = pattern.matcher(ingredient.trim());
////            if (matcher.find()) {
////                String ingredientName = matcher.group(1).trim();
////                String quantity = matcher.group(2) != null ? matcher.group(2).trim() : ""; // Количество может быть отсутствующим
////                String unitOfMeasure = matcher.group(3).trim();
////
////                log.info("Name of the ingredient: '{}'", ingredientName);
////                log.info("Quantity: '{}'", quantity);
////                log.info("Unit of measurement: '{}'", unitOfMeasure);
////
////                ingredients.add(new Ingredient(ingredientName, Integer.parseInt(quantity), unitOfMeasure));
////            } else {
////                log.error("The text with the parts of the ingredient does not match the expected format: {}", parsedIngredients);
////            }
////        }
////        return ingredients;
////    }
//
//    private List<String> parseInstructions(String text) {
//        return Arrays.stream(text.split("\\. "))
//                .map(i -> i.replace(".", "").trim())
//                .map(i -> {
//                    if (!i.isEmpty()) {
//                        return i.substring(0, 1).toUpperCase() + i.substring(1);
//                    }
//                    return i;
//                })
//                .toList();
//    }
}
