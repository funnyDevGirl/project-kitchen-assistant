package io.project.kitchen_assistant.service.impl;

import io.project.kitchen_assistant.component.DataStorage;
import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.dto.todoist.auth.TodoistToken;
import io.project.kitchen_assistant.exception.UserNotFoundException;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.service.StateService;
import io.project.kitchen_assistant.service.TodoistAuthorizeService;
import io.project.kitchen_assistant.service.UserService;
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
    private final UserRepository userRepository;
    private final UserService userService;
    private final DataStorage authorizationCodeStorage;

    public TodoistAuthorizeServiceImpl(AppConfig appConfig, StateService stateService, UserRepository userRepository,
                                       @Qualifier("restTemplateForGetTodoistToken") RestTemplate restTemplateForGetTodoistToken,
                                       UserService userService, DataStorage authorizationCodeStorage) {
        this.appConfig = appConfig;
        this.stateService = stateService;
        this.userRepository = userRepository;
        this.restTemplateForGetTodoistToken = restTemplateForGetTodoistToken;
        this.userService = userService;
        this.authorizationCodeStorage = authorizationCodeStorage;
    }

    @Override
    public String buildAuthUrl() {
        String state = UUID.randomUUID().toString();
        LocalDateTime ttl = LocalDateTime.now().plusHours(1);

        log.debug("Saving State begins");
        stateService.saveState(state, "in_progress", ttl);

        UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl(appConfig.getTodoistAuthorizationUri())
                .queryParam("client_id", appConfig.getTodoistClientId())
                .queryParam("scope", appConfig.getTodoistScope())
                .queryParam("state", state)
                .build();

        log.info("Redirecting to: {}", uriComponents.toUriString());

        return uriComponents.toUriString();
    }

    @Override
    public TodoistToken exchangeToken(String code) {

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return null;
//        }
//        String email = authentication.getName();
        String email = userService.getCurrentUser(); // верен ли метод?

        authorizationCodeStorage.save(email, code);

        ResponseEntity<TodoistToken> responseEntity = restTemplateForGetTodoistToken.exchange
                (appConfig.getExchangeTodoistTokenUri(), HttpMethod.POST, null, TodoistToken.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("The POST request was completed successfully, the status code '{}' was returned",
                    responseEntity.getStatusCode().value());

            TodoistToken token = responseEntity.getBody();

            if (token != null) {
                User user = userRepository.findByEmail(email).orElseThrow(
                        () -> new UserNotFoundException(String.format("User with email: '%s' not found", email)));

                user.setTodoistToken(token.generateBearer());

                userRepository.save(user);
                log.debug("Access token has been successfully added to the user with email '{}'", user.getEmail());

                authorizationCodeStorage.remove(email);
            }
            return token;

        } else {
            throw new RuntimeException(format("Failed to exchange token: %s", responseEntity.getStatusCode().value()));
        }
    }
}
