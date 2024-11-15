package io.project.kitchen_assistant.controller;

import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.dto.todoist.auth.TodoistToken;
import io.project.kitchen_assistant.service.TodoistAuthorizeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class TodoistAuthorizeController {

    private final AppConfig appConfig;
    private final TodoistAuthorizeService todoistAuthorizeService;

    @GetMapping("/authorize")
    public RedirectView authorize() {
        String authorizationUrl = todoistAuthorizeService.buildAuthUrl();

        return new RedirectView(authorizationUrl);
    }

    @GetMapping("/callback")
    @ResponseStatus(HttpStatus.OK)
    public TodoistToken authorizationCallback(@RequestParam("code") String code,
                                              @RequestParam("state") String state) {
        log.debug("Received code: '{}'", code);

        if (!state.equals("тут будет state, сгенерированный выше")) {
            throw new IllegalArgumentException(
                    "Suspicious activity has been recorded. The process has been stopped.");
        }
        log.info("State is valid. Getting access token to Todoist begins");

        return todoistAuthorizeService.exchangeToken(code);
    }
}
