package io.project.kitchen_assistant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import io.project.kitchen_assistant.dto.recipes.RecipeDTO;
import io.project.kitchen_assistant.dto.recipes.gpt.GptResponse;
import io.project.kitchen_assistant.exception.RecipeNotFoundException;
import io.project.kitchen_assistant.formatter.RecipesParser;
import io.project.kitchen_assistant.mapper.RecipeMapper;
import io.project.kitchen_assistant.mapper.UserMapper;
import io.project.kitchen_assistant.model.Recipe;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.RecipeRepository;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.service.impl.RecipeServiceImpl;
import io.project.kitchen_assistant.utils.FileReader;
import io.project.kitchen_assistant.utils.FileReaderForTests;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;

public class RecipeServiceImplTest {

    @InjectMocks
    private RecipeServiceImpl recipeService;

    @Mock
    private AppConfig appConfig;

    @Mock
    private RecipesParser recipesParser;

    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private RestTemplate restTemplateGptApi;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileReaderForTests fileReader;

    private ObjectMapper objectMapper;

    private Recipe testRecipe;

    private RecipeCreateDTO createDTO;

    private User testUser;

    private String query;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();

        when(appConfig.getGptApiUrl()).thenReturn("http://fake-url.com");

        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Chuck");
        testUser.setLastName("Norris");
        testUser.setEmail("test@example.com");
        testUser.setPasswordDigest("qwerty");

        createDTO = new RecipeCreateDTO("Пирог",
                List.of("форель - 500г", "лук - 200г", "тесто - любое"),
                "Ингредиенты нарезать. Завернуть в тесто.");

        testRecipe = new Recipe(5L, "Пирог",
                List.of("форель - 500г", "лук - 200г", "тесто - любое"),
                "Ингредиенты нарезать. Завернуть в тесто.", testUser);

        query = "вишня";
    }

    @Test
    void testSearchRecipesSuccess() throws Exception {
        // Arrange
        String requestBody = FileReaderForTests.readFixture("requestBodyForGpt.json");
        String response = FileReaderForTests.readFixture("GptResponse.json");

        GptResponse mockResponse = objectMapper.readValue(response, GptResponse.class);
        ResponseEntity<GptResponse> responseEntity = ResponseEntity.ok(mockResponse);

        // Mock RestTemplate
        when(restTemplateGptApi.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(GptResponse.class)))
                .thenReturn(responseEntity);

        // Mock parsing
        List<RecipeCreateDTO> expectedRecipes = List.of(

                new RecipeCreateDTO("Пирог с вишней", List.of(
                        "вишня — 500 г", "мука — 250 г", "сахар — 200 г", "яйца — 3 шт.", "ванильный сахар — 1 ч. л"),
                        "Разогреть духовку до 180 °C. Выпекать в духовке около 40–45 минут, до готовности."),

                new RecipeCreateDTO("Компот из вишни",
                        List.of("вишня — 500 г", "вода — 2 л", "сахар — 200 г"), format("%s %s",
                        "В кастрюлю налить воду, добавить сахар и довести до кипения.",
                        "Добавить вишню в кипящий сироп и варить на среднем огне около 15 минут.")),

                new RecipeCreateDTO("Вишнёвый десерт", List.of(
                        "вишня — 300 г", "сливки — 200 мл", "сахар — 100 г", "желатин — 1 ст. л.", "вода — 50 мл."),
                        "Всё подготовить и приготовить.")
        );
        when(recipesParser.parseRecipesText(any())).thenReturn(expectedRecipes);

        // Act
        List<RecipeCreateDTO> recipes = recipeService.searchRecipes(query);

        // Assert
        assertNotNull(recipes);
        assertEquals(3, recipes.size());
        verify(restTemplateGptApi).exchange(anyString(),
                eq(HttpMethod.POST), any(HttpEntity.class), eq(GptResponse.class));
    }

    @Test
    void testSearchRecipesNoResults() throws Exception {
        GptResponse mockResponse = new GptResponse();
        ResponseEntity<GptResponse> responseEntity = ResponseEntity.ok(mockResponse);

        when(restTemplateGptApi.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(GptResponse.class)))
                .thenReturn(responseEntity);

        when(recipesParser.parseRecipesText(any())).thenReturn(List.of());

        // Act
        List<RecipeCreateDTO> recipes = recipeService.searchRecipes(query);

        // Assert
        assertNotNull(recipes);
        assertTrue(recipes.isEmpty());
    }

    @Test
    void testSearchRecipesThrowsHttpClientErrorException() throws Exception {
        when(restTemplateGptApi.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(GptResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // Act
        List<RecipeCreateDTO> recipes = recipeService.searchRecipes(query);

        // Assert
        assertNotNull(recipes);
        assertTrue(recipes.isEmpty());
    }

    @Test
    void testCreateRecipeWhenSuccess() {
        // Arrange
        String existingEmail = "test@example.com";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(recipeMapper.map(createDTO)).thenReturn(testRecipe);
        when(recipeRepository.save(testRecipe)).thenReturn(testRecipe);

        RecipeDTO expectedRecipeDTO = new RecipeDTO(5L, "Пирог",
                List.of("форель - 500г", "лук - 200г", "тесто - любое"),
                "Ингредиенты нарезать. Завернуть в тесто.");

        when(recipeMapper.map(testRecipe)).thenReturn(expectedRecipeDTO);

        // Act
        RecipeDTO result = recipeService.create(createDTO, existingEmail);

        // Assert
        assertNotNull(result);
        assertEquals(expectedRecipeDTO.getId(), result.getId());
        assertEquals(expectedRecipeDTO.getName(), result.getName());
        verify(recipeRepository).save(testRecipe);
    }

    @Test
    void testCreateRecipeWhenUserNotFound() {
        // Arrange
        String existingEmail = "EmailNotFound@example.com";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Assert
        assertThrows(UsernameNotFoundException.class, () ->
                recipeService.create(createDTO, existingEmail));
    }

    @Test
    void testGetAllRecipesByUserEmailWhenRecipesAreFound() {
        // Arrange
        String userEmail = testUser.getEmail();
        List<Recipe> recipes = List.of(testRecipe);

        RecipeDTO expectedRecipeDTO = new RecipeDTO(5L, "Пирог",
                List.of("форель - 500г", "лук - 200г", "тесто - любое"),
                "Ингредиенты нарезать. Завернуть в тесто.");

        when(recipeRepository.findAllByUserEmail(userEmail)).thenReturn(recipes);
        when(recipeMapper.map(testRecipe)).thenReturn(expectedRecipeDTO);

        // Act
        List<RecipeDTO> result = recipeService.getAllRecipesByUserEmail(userEmail);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedRecipeDTO, result.getFirst());
        verify(recipeRepository).findAllByUserEmail(userEmail);
    }

    @Test
    void testGetAllRecipesByUserEmailWhenNoRecipesFound() {
        // Arrange
        String userEmail = testUser.getEmail();
        when(recipeRepository.findAllByUserEmail(userEmail)).thenReturn(List.of());

        // Act
        List<RecipeDTO> result = recipeService.getAllRecipesByUserEmail(userEmail);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recipeRepository).findAllByUserEmail(userEmail);
    }

    @Test
    void testFindByIdWhenRecipeExists() {
        // Arrange
        long recipeId = 5L;
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(testRecipe));
        RecipeDTO expectedRecipeDTO = new RecipeDTO(5L, "Пирог",
                List.of("форель - 500г", "лук - 200г", "тесто - любое"),
                "Ингредиенты нарезать. Завернуть в тесто.");

        when(recipeMapper.map(testRecipe)).thenReturn(expectedRecipeDTO);

        // Act
        RecipeDTO result = recipeService.findById(recipeId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedRecipeDTO.getId(), result.getId());
        assertEquals(expectedRecipeDTO.getName(), result.getName());
        verify(recipeRepository).findById(recipeId);
    }

    @Test
    void testFindByIdWhenRecipeNotFound() {
        // Arrange
        long notExistingRecipeId = 999L;
        when(recipeRepository.findById(notExistingRecipeId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(RecipeNotFoundException.class, () -> {
            recipeService.findById(notExistingRecipeId);
        });
    }

    @Test
    void testDeleteWhenRecipeExists() {
        // Arrange
        long recipeId = 5L;
        LogCaptor logCaptor = LogCaptor.forClass(RecipeServiceImpl.class);

        // Act
        recipeService.delete(recipeId);

        // Assert
        verify(recipeRepository).deleteById(recipeId);

        List<String> logs = logCaptor.getInfoLogs();
        assertThat(logs).contains("Recipe with id '5' successfully deleted");
    }

    @Test
    void testCreateRequestBodySuccess() throws Exception {
        String expected = recipeService.createRequestBody(query);
        String actual = FileReaderForTests.readFixture("requestBodyForGpt.json");

        //Assert
        assertEquals(expected, actual);
    }

    @Test
    void testCreateRequestBodyThrowsException() {
        try (MockedStatic<FileReader> mockedStatic = mockStatic(FileReader.class)) {
            mockedStatic.when(() -> FileReader.readResourceFile("frameForRequestToGpt.json"))
                    .thenThrow(new IOException("Resource not found"));

            // Act & Assert
            Exception exception = assertThrows(Exception.class, () -> {
                recipeService.createRequestBody(query);
            });

            // Assert
            assertEquals("Failed to read the request body JSON file", exception.getMessage());
            assertEquals("Resource not found", exception.getCause().getMessage());
        }
    }
}
