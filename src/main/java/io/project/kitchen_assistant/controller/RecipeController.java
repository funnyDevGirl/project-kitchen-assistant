package io.project.kitchen_assistant.controller;

import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import io.project.kitchen_assistant.dto.recipes.RecipeDTO;
import io.project.kitchen_assistant.service.RecipeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/recipes")
@Validated
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

        String email = authentication.getName();
        return recipeService.create(recipeCreateDTO, email);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<RecipeDTO>> getFavoriteRecipes(Authentication authentication) {

        List<RecipeDTO> recipes = recipeService.getAllRecipesByUserEmail(authentication.getName());
        return ResponseEntity
                .ok()
                .header("X-Total-Count", String.valueOf(recipes.size()))
                .body(recipes);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecipeDTO show(@PathVariable Long id) {
        return recipeService.findById(id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        recipeService.delete(id);
    }
}
