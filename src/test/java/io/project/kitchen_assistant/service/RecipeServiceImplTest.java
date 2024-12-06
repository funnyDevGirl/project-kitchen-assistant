package io.project.kitchen_assistant.service;

import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import io.project.kitchen_assistant.dto.recipes.RecipeDTO;
import io.project.kitchen_assistant.exception.RecipeNotFoundException;
import io.project.kitchen_assistant.formatter.RecipesParser;
import io.project.kitchen_assistant.mapper.RecipeMapper;
import io.project.kitchen_assistant.mapper.UserMapper;
import io.project.kitchen_assistant.model.Recipe;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.RecipeRepository;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.service.impl.RecipeServiceImpl;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    private Recipe testRecipe;

    private RecipeCreateDTO createDTO;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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
    }

    @Test
    void testCreateRecipeWhenSuccess() {
        // Arrange
        String existingEmail = "test@example.com";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(recipeMapper.map(createDTO)).thenReturn(testRecipe);
        when(recipeRepository.save(testRecipe)).thenReturn(testRecipe);

        RecipeDTO expectedRecipeDTO = new RecipeDTO(5L,"Пирог",
                List.of("форель - 500г", "лук - 200г", "тесто - любое"),
                "Ингредиенты нарезать. Завернуть в тесто.");

        when(recipeMapper.map(testRecipe)).thenReturn(expectedRecipeDTO);

        // Act
        RecipeDTO result = recipeService.create(createDTO, existingEmail);

        // Assert
        assertNotNull(result);
        assertEquals(expectedRecipeDTO.getId(), result.getId());
        assertEquals(expectedRecipeDTO.getName(), result.getName());
        verify(recipeRepository, times(1)).save(testRecipe);
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
        verify(recipeRepository, times(1)).findAllByUserEmail(userEmail);
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
        verify(recipeRepository, times(1)).findAllByUserEmail(userEmail);
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
        verify(recipeRepository, times(1)).findById(recipeId);
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
        verify(recipeRepository, times(1)).deleteById(recipeId);

        List<String> logs = logCaptor.getInfoLogs();
        assertThat(logs).contains("Recipe with id '5' successfully deleted");
    }
}
