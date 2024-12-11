package io.project.kitchen_assistant.controller;

import io.project.kitchen_assistant.dto.todoist.auth.TodoistToken;
import io.project.kitchen_assistant.handler.ErrorResponse;
import io.project.kitchen_assistant.model.State;
import io.project.kitchen_assistant.repository.StateRepository;
import io.project.kitchen_assistant.service.TodoistAuthorizeService;
import io.project.kitchen_assistant.utils.JWTUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import java.net.URI;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
@Validated
public class TodoistAuthorizeController {

    private final JWTUtils jwtUtils;
    private final TodoistAuthorizeService todoistAuthorizeService;
    private final StateRepository stateRepository;

    /**
     * Перенаправляет пользователя на страницу авторизации в сервисе Todoist.
     *
     * <p>
     * Этот метод вызывается, когда у пользователя отсутствует access token.
     * Метод формирует URL для авторизации на основе email текущего пользователя
     * и производит редирект в новое окно.
     * </p>
     *
     * @param authentication объект {@link Authentication}, содержащий детали текущей аутентификации пользователя.
     * @return {@link RedirectView} объект, представляющий редирект на URL для авторизации в Todoist.
     */

    @Operation(summary = "Redirect to Todoist authorization page",
            description = "Redirects the user to the Todoist authorization page when the user lacks an access token. "
                    + "The URL is generated based on the current user's email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirects user to Todoist authorization page",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "401", description = "User is not authenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"401\", \"message\": "
                                    + "\"User is not authenticated\"}")})
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"500\", \"message\": "
                                    + "\"Internal Server Error\"}")}))
    })
    @GetMapping("/authorize")
    public RedirectView authorize(Authentication authentication) {
        log.info("Authorize on Todoist service begins");

        String currentUserEmail = getUserEmail(authentication);
        String authorizationUrl = todoistAuthorizeService.buildAuthUrl(currentUserEmail);

        return new RedirectView(authorizationUrl);
    }

    /**
     * Обрабатывает обратный вызов после успешной аутентификации пользователя в сервисе Todoist.
     *
     * <p>
     * Этот метод вызывается после успешной аутентификации пользователя
     * в сервисе Todoist. Он извлекает код и состояние из параметров запроса,
     * проверяет и восстанавливает состояние, обновляет информацию об
     * аутентификации пользователя с помощью JWT токена и извлекает
     * access token от Todoist. В случае успешной аутентификации
     * пользователь перенаправляется на указанный URI.
     * </p>
     *
     * @param code параметр, полученный от сервиса Todoist, содержащий код авторизации.
     * @param state параметр, полученный от сервиса Todoist, использованный для защиты от CSRF-атак.
     * @return {@link ResponseEntity<Void>} с HTTP статусом 200 OK, если процесс завершился успешно,
     *         или с HTTP статусом 500 Internal Server Error в случае ошибки при получении token.
     *         В случае успешной аутентификации возвращает статус 302 Found и URI для закрытия окна.
     * @throws IllegalArgumentException если предоставлено некорректное состояние.
     */

    @Operation(summary = "Handle authorization callback",
            description = "Handles the callback from Todoist after user authorization. Validates the state, "
                    + "retrieves the access token using the provided code, and updates the authentication context.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Authentication updated successfully, redirecting to close page",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "400", description = "Invalid state provided",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"400\", \"message\": "
                                    + "\"Invalid state provided. The process has been stopped.\"}")})
            ),
            @ApiResponse(responseCode = "500", description = "Failed to retrieve access token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(value = "{\"code\": \"500\", \"message\": "
                                    + "\"Internal Server Error\"}")}))
    })
    @GetMapping("/callback")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> authorizationCallback(@RequestParam("code") String code,
                                                      @RequestParam("state") String state) {

        State savedState = stateRepository.findByUuid(state).orElseThrow(
                () -> new IllegalArgumentException("Invalid state provided. The process has been stopped."));

        String emailFromState = savedState.getUserEmail();
        log.debug("Email from State: {}", emailFromState);

        log.info("State is valid. Getting access token to Todoist begins");

        final var jwtToken = jwtUtils.buildAuthToken(emailFromState);
        SecurityContextHolder.getContext().setAuthentication(jwtToken);

        log.info("Authentication for the user with email '{}' has been successfully updated.", emailFromState);

        TodoistToken savedToken =  todoistAuthorizeService.exchangeToken(code, emailFromState);

        if (savedToken == null) {
            log.error("Failed to retrieve access token.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("/api/v1/auth/close"))
                .build();
    }

    /**
     * Закрывает текущее окно на стороне клиента.
     *
     * <p>
     * Этот метод предназначен только для вызова из клиентского интерфейса
     * для закрытия окна. Не предназначен для других целей или
     * использования в других контекстах.
     * </p>
     *
     * {@code @responseStatus} HttpStatus.OK если окно успешно закрыто.
     */

    @Operation(summary = "Close the authorization tab",
            description = "This endpoint is called to indicate that the authorization process has finished. "
                    + "It can be used to close the authorization tab.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tab closed successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = {@ExampleObject(value = "{\"code\": \"500\", \"message\": "
                                + "\"Internal Server Error\"}")}))
    })
    @GetMapping("/close")
    @ResponseStatus(HttpStatus.OK)
    public void closeTab() {
    }

    protected String getUserEmail(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            return authentication.getName();

        } else {
            throw new IllegalStateException("User is not authenticated");
        }
    }
}
