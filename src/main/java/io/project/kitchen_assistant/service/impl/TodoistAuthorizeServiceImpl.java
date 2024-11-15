package io.project.kitchen_assistant.service.impl;

import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.config.AuthorizationCodeContext;
import io.project.kitchen_assistant.dto.todoist.auth.TodoistToken;
import io.project.kitchen_assistant.service.StateService;
import io.project.kitchen_assistant.service.TodoistAuthorizeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.LocalDateTime;
import java.util.UUID;
import static java.lang.String.format;

@Slf4j
@Service
public class TodoistAuthorizeServiceImpl implements TodoistAuthorizeService {

    private final AppConfig appConfig;
    private final RestTemplate restTemplateForGetTodoistToken;
    private final StateService stateService;

    public TodoistAuthorizeServiceImpl(AppConfig appConfig, StateService stateService,
                                       @Qualifier("restTemplateForGetTodoistToken") RestTemplate restTemplateForGetTodoistToken) {
        this.appConfig = appConfig;
        this.stateService = stateService;
        this.restTemplateForGetTodoistToken = restTemplateForGetTodoistToken;
    }

    @Override
    public String buildAuthUrl() {
        String state = UUID.randomUUID().toString();
        LocalDateTime ttl = LocalDateTime.now().plusHours(1);

        log.debug("Saving State begins");
        stateService.saveState(state, "in_progress", ttl);

        UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl("https://todoist.com/oauth/authorize")
                .queryParam("client_id", appConfig.getTodoistClientId())
                .queryParam("scope", appConfig.getTodoistScope())
                .queryParam("state", state)
                .build();

        log.info("Redirecting to: {}", uriComponents.toUriString());

        return uriComponents.toUriString();
    }

    @Override
    public TodoistToken exchangeToken(String code) {

        AuthorizationCodeContext.setAuthorizationCode(code);

        ResponseEntity<TodoistToken> responseEntity = restTemplateForGetTodoistToken.exchange
                (appConfig.getExchangeTodoistTokenUrl(), HttpMethod.POST, null, TodoistToken.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("The POST request was completed successfully, the status code '{}' was returned",
                    responseEntity.getStatusCode().value());

            TodoistToken token = responseEntity.getBody();

            if (token != null) {
                appConfig.setTodoistApiToken(token.generateBearer());

                AuthorizationCodeContext.clear();
            }
            return token;

        } else {
            throw new RuntimeException(format("Failed to exchange token: %s", responseEntity.getStatusCode().value()));
        }
    }
}
