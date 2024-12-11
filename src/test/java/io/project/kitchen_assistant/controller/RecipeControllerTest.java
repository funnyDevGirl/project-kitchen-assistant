package io.project.kitchen_assistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.project.kitchen_assistant.container.PostgresContainerManager;
import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import io.project.kitchen_assistant.dto.users.UserModificationDTO;
import io.project.kitchen_assistant.mapper.RecipeMapper;
import io.project.kitchen_assistant.mapper.UserMapper;
import io.project.kitchen_assistant.model.Recipe;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.RecipeRepository;
import io.project.kitchen_assistant.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.lang.String.format;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class RecipeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeMapper recipeMapper;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext wac;

    private Recipe testRecipe;

    private User testUser;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private final PostgreSQLContainer<?> postgresContainer =
            PostgresContainerManager.getContainer();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        UserModificationDTO modificationDTO = new UserModificationDTO(
                "test@example.com", "Chuck", "Norris", "qwerty");
        testUser = userMapper.toUser(modificationDTO);
        User savedUser = userRepository.save(testUser);

        token = jwt().jwt(builder -> builder.subject(savedUser.getEmail()));

        RecipeCreateDTO dto = new RecipeCreateDTO("Пирог",
                List.of("форель - 500г", "лук - 200г", "тесто - любое"),
                "Ингредиенты нарезать. Завернуть в тесто.");

        testRecipe = recipeMapper.map(dto);
        testRecipe.setUser(testUser);

        recipeRepository.save(testRecipe);
    }

    @AfterEach
    public void clean() {
        recipeRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/v1/recipes/{id}", testRecipe.getId());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("ingredients").isEqualTo(testRecipe.getIngredients()),
                v -> v.node("instructions").isEqualTo(testRecipe.getInstructions())
        );
    }

    @Test
    public void testShowNotFound() throws Exception {
        Long nonExistentId = 999L;

        var request = get("/api/v1/recipes/{id}", nonExistentId);

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().string(format("{\"code\":404,\"message\":\"Recipe with id: '%s' not found\"}",
                        nonExistentId)));
    }

    @Test
    public void testGetFavoriteRecipes() throws Exception {
        var result = mockMvc.perform(get("/api/v1/recipes").with(token))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        RecipeCreateDTO dto = new RecipeCreateDTO("Салат лёгкий",
                List.of("капуста - 200г", "кинза - 50г", "масло оливковое"),
                "Капусту нарезать соломкой. Добавить кинзу и немного масла.");

        mockMvc.perform(post("/api/v1/recipes")
                        .with(token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Recipe savedRecipe = recipeRepository.findByName(dto.getName()).orElseThrow();

        assertThat(savedRecipe.getName()).isEqualTo(dto.getName());
        assertThat(savedRecipe.getIngredients()).isEqualTo(dto.getIngredients());
        assertThat(savedRecipe.getInstructions()).isEqualTo(dto.getInstructions());
    }

    @Test
    public void testDeleteAnExistingEvent() throws Exception {
        var request = delete("/api/v1/recipes/{id}", testRecipe.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(recipeRepository.existsById(testRecipe.getId())).isEqualTo(false);
    }
}
