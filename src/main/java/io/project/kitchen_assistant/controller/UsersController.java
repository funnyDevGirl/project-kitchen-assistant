package io.project.kitchen_assistant.controller;

import io.project.kitchen_assistant.dto.users.CreateUserGroup;
import io.project.kitchen_assistant.dto.users.UpdateUserGroup;
import io.project.kitchen_assistant.dto.users.UserDTO;
import io.project.kitchen_assistant.dto.users.UserModificationDTO;
import io.project.kitchen_assistant.handler.ErrorResponse;
import io.project.kitchen_assistant.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UsersController {

    private final UserService userService;

    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = {@ExampleObject(
                                    value = "{\"id\": 1, \"email\": \"user@gmail.com\", \"firstName\": \"Alina\", "
                                            + "\"lastName\": \"Tarasova\", \"createdAt\": \"2023-01-01\"}")})
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"400\", \"message\": "
                                    + "\"Incorrect email format\"}")}))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@RequestBody @Validated(CreateUserGroup.class) UserModificationDTO modificationDTO) {
        return userService.create(modificationDTO);
    }


    @Operation(summary = "Get the current user", description = "Returns the current authenticated user's details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current user details",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = {@ExampleObject(
                                    value = "{\"id\": 1, \"email\": \"user@gmail.com\", \"firstName\": \"Alina\", "
                                    + "\"lastName\": \"Tarasova\", \"createdAt\": \"2023-01-01\"}")})
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"401\", \"message\": "
                                    + "\"User is not authenticated\"}")})
            ),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"404\", \"message\": "
                                    + "\"User with id '5' not found\"}")}))
    })
    @GetMapping(path = "/current")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO showCurrentUser(Authentication authentication) {
        return userService.findByEmail(authentication.getName());
    }


    @Operation(summary = "Get user by ID", description = "Retrieves user details by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = {@ExampleObject(
                                    value = "{\"id\": 1, \"email\": \"user@gmail.com\", \"firstName\": \"Alina\", "
                                            + "\"lastName\": \"Tarasova\", \"createdAt\": \"2023-01-01\"}")})
            ),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"404\", \"message\": "
                                    + "\"User with id '5' not found\"}")}))
    })
    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }


    @Operation(summary = "Update user", description = "Updates the user details for the given ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = {@ExampleObject(
                                    value = "{\"id\": 1, \"email\": \"user@gmail.com\", \"firstName\": \"Akilina\", "
                                            + "\"lastName\": \"Tarasova\", \"createdAt\": \"2023-01-01\"}")})
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"400\", \"message\": "
                                    + "\"Incorrect email format\"}")})
            ),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"404\", \"message\": "
                                    + "\"User with id '5' not found\"}")}))
    })
    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@userUtils.isUser(#id)")
    public UserDTO update(@PathVariable("id") Long id,
            @RequestBody @Validated(UpdateUserGroup.class) UserModificationDTO modificationDTO) {
        return userService.update(modificationDTO, id);
    }


    @Operation(summary = "Delete a user", description = "Deletes the user with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully",
                    content = @Content()),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"404\", \"message\": "
                                    + "\"User with id '3' not found\"}")}))
    })
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@userUtils.isUser(#id)")
    public void delete(@PathVariable("id") Long id) throws Exception {
        userService.delete(id);
    }
}
