package io.project.kitchen_assistant.controller;

import io.project.kitchen_assistant.dto.recipes.RecipeCreateDTO;
import io.project.kitchen_assistant.dto.recipes.RecipeDTO;
import io.project.kitchen_assistant.handler.ErrorResponse;
import io.project.kitchen_assistant.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/recipes")
@Validated
public class RecipeController {

    private final RecipeService recipeService;

    @Operation(
            summary = "User login to the application",
            description = "Performs user login to the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful search result",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "array",
                                    implementation = RecipeCreateDTO.class),
                            examples = {@ExampleObject(value = "[{\"name\": \"Вишневый пирог\", \"ingredients\": "
                                    + "[\"Вишня - 500г\", \"Тесто - 1кг\"], \"instructions\": "
                                    + "\"Раскатать тесто, выложить вишню. Запекать 40мин при 180гр.\"}]")})
            ),
            @ApiResponse(responseCode = "400", description = "Error when getting the recipe list",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"400\", "
                                    + "\"message\": \"Error when getting the recipe list\"}")})
            )
    })
    @PostMapping(value = "/search")
    public List<RecipeCreateDTO> searchRecipes(@RequestBody String query) throws Exception {
        return recipeService.searchRecipes(query);
    }


    @Operation(
            summary = "Create a new recipe",
            description = "Creates a new recipe and returns the created recipe's information."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful search result",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RecipeDTO.class),
                            examples = {@ExampleObject(value = "{\"id\": 12, \"name\": \"Вишневый пирог\", "
                                    + "\"ingredients\": [\"Вишня - 500г\", \"Тесто - 1кг\"], \"instructions\": "
                                    + "\"Раскатать тесто, выложить вишню. Запекать 40мин при 180гр.\"}")})
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(
                                    value = "{\"code\": \"400\", \"message\": \"Invalid recipe data\"}")})
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized, authentication is required",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(
                                    value = "{\"code\": \"401\", \"message\": \"Authentication is required\"}")})
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeDTO create(@Valid @RequestBody RecipeCreateDTO recipeCreateDTO,
                            Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication is required");
        }
        String email = authentication.getName();
        return recipeService.create(recipeCreateDTO, email);
    }


    @Operation(
            summary = "Get favorite recipes for the authenticated user",
            description = "Retrieves a list of favorite recipes for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite recipes retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "array",
                                    implementation = RecipeDTO.class),
                            examples = {@ExampleObject(value = "[{\"id\": 12, \"name\": \"Вишневый пирог\", "
                                    + "\"ingredients\": [\"Вишня - 500г\", \"Тесто - 1кг\"], \"instructions\": "
                                    + "\"Раскатать тесто, выложить вишню. Запекать 40мин при 180гр.\"}]")})
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized, authentication is required",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(
                                    value = "{\"code\": \"401\", \"message\": \"Authentication is required\"}")})
            ),
            @ApiResponse(responseCode = "404", description = "No favorite recipes found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(
                                    value = "{\"code\": \"404\", \"message\": \"No favorite recipes found\"}")})
            )
    })
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


    @Operation(
            summary = "Get a recipe by ID",
            description = "Retrieves the recipe with the specified ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RecipeDTO.class),
                            examples = {@ExampleObject(value = "{\"id\": 12, \"name\": \"Вишневый пирог\", "
                                    + "\"ingredients\": [\"Вишня - 500г\", \"Тесто - 1кг\"], \"instructions\": "
                                    + "\"Раскатать тесто, выложить вишню. Запекать 40мин при 180гр.\"}")})
            ),
            @ApiResponse(responseCode = "404", description = "Recipe not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(
                                    value = "{\"code\": \"404\", \"message\": \"Recipe not found\"}")})
            ),
            @ApiResponse(responseCode = "400", description = "Invalid recipe ID supplied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(
                                    value = "{\"code\": \"400\", \"message\": \"Invalid recipe ID\"}")})
            )
    })
    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecipeDTO show(@PathVariable("id") Long id) {
        return recipeService.findById(id);
    }


    @Operation(
            summary = "Delete a recipe by ID",
            description = "Deletes the specified recipe from the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recipe deleted successfully",
                    content = @Content()
            ),
            @ApiResponse(responseCode = "404", description = "Recipe not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(
                                    value = "{\"code\": \"404\", \"message\": \"Recipe not found\"}")})
            ),
            @ApiResponse(responseCode = "400", description = "Invalid recipe ID supplied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(
                                    value = "{\"code\": \"400\", \"message\": \"Invalid recipe ID\"}")}))
    })
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") long id) {
        recipeService.delete(id);
    }
}
