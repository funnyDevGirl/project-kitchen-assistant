package io.project.kitchen_assistant.controller;

import io.project.kitchen_assistant.dto.users.AuthRequest;
import io.project.kitchen_assistant.handler.ErrorResponse;
import io.project.kitchen_assistant.utils.JWTUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@Validated
public class AuthenticationController {

    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Operation(
            summary = "User login to the application",
            description = "Performs user login to the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful authorization",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "string",
                                    description = "JWT token for user authentication",
                                    example = "Rvkj37yLKNgb349nlj"))
            ),
            @ApiResponse(responseCode = "400", description = "Incorrect username or password",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"400\", \"message\": "
                                    + "\"Incorrect username or password\"}")})
            ),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"500\", \"message\": "
                                    + "\"Internal Server Error\"}")}))
    })
    @PostMapping("/login")
    public String create(@RequestBody @Valid AuthRequest authRequest) {
        var authentication = new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(), authRequest.getPassword());

        authenticationManager.authenticate(authentication);

        return jwtUtils.generateToken(authRequest.getUsername());
    }
}
