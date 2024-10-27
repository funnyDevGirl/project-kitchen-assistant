package io.project.kitchen_assistant.controller;

import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import io.project.kitchen_assistant.dto.recipes.RecipeDTO;
import io.project.kitchen_assistant.service.RecipeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping(value = "/search")
    public List<RecipeCreateDTO> searchRecipes(@RequestBody String query) throws Exception {
        return recipeService.searchRecipes(query);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeDTO create(@Valid @RequestBody RecipeCreateDTO recipeCreateDTO,
                            Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication is required");
        }
        String email = authentication.getName(); // Получаю email текущего пользователя
        return recipeService.create(recipeCreateDTO, email); // Использую email для создания рецепта
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<RecipeDTO>> getFavoriteRecipes(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication is required");
        }

        List<RecipeDTO> recipes = recipeService.getAllRecipesByUserEmail(authentication.getName());
        return ResponseEntity
                .ok()
                .header("X-Total-Count", String.valueOf(recipes.size()))
                .body(recipes);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecipeDTO show(@PathVariable long id) {
        return recipeService.findById(id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        recipeService.delete(id);
    }
}
