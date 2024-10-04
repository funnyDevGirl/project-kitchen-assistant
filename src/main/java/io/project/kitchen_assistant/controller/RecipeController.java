package io.project.kitchen_assistant.controller;

import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import io.project.kitchen_assistant.dto.recipes.RecipeDTO;
import io.project.kitchen_assistant.service.RecipeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping("/search")
    public List<RecipeCreateDTO> searchRecipes(@RequestBody String query) { // выдавать строку
        return recipeService.searchRecipes(query);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeDTO create(@Valid @RequestBody RecipeCreateDTO recipeCreateDTO) {
        return recipeService.create(recipeCreateDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<RecipeDTO>> getAll() {
        List<RecipeDTO> recipes = recipeService.getAll();
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
    @PreAuthorize("@userUtils.isUser(#id)")
    public void delete(@PathVariable Long id) throws Exception {
        recipeService.delete(id);
    }
}
