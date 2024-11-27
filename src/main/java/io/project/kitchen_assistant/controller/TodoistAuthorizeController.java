package io.project.kitchen_assistant.controller;

import io.project.kitchen_assistant.component.DataStorage;
import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.dto.todoist.auth.TodoistToken;
import io.project.kitchen_assistant.model.State;
import io.project.kitchen_assistant.repository.StateRepository;
import io.project.kitchen_assistant.service.TodoistAuthorizeService;
import io.project.kitchen_assistant.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class TodoistAuthorizeController {

    private final AppConfig appConfig;
    private final TodoistAuthorizeService todoistAuthorizeService;
    private final StateRepository stateRepository;
    private final DataStorage authorizationCodeStorage;

    @GetMapping("/authorize")
    public RedirectView authorize() {
        log.info("Authorize on Todoist service begins");

        String authorizationUrl = todoistAuthorizeService.buildAuthUrl();

        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/callback")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> authorizationCallback(@RequestParam("code") String code,
                                                      @RequestParam("state") String state) {

        Optional<State> savedState = stateRepository.findByUuid(state);

        if (savedState.isEmpty()) {
            throw new IllegalArgumentException("Invalid state provided. The process has been stopped.");
        }
        log.info("State is valid. Getting access token to Todoist begins");

        String currentUserEmail = savedState.get().getUserEmail();

        TodoistToken savedToken =  todoistAuthorizeService.exchangeToken(code, currentUserEmail);
        log.info("ТОКЕН существует? {}", savedToken != null); // досюда не доходит

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("/api/v1/recipes"))
                .build();
    }
}
