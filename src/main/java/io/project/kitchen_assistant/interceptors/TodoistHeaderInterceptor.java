package io.project.kitchen_assistant.interceptors;

import io.project.kitchen_assistant.component.DataStorage;
import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.exception.UserNotFoundException;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Slf4j
@AllArgsConstructor
@Component
public class TodoistHeaderInterceptor implements ClientHttpRequestInterceptor {

    private final UserRepository userRepository;
    private final DataStorage authorizationCodeStorage;
    private final UserService userService;
    private final AppConfig appConfig;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        log.info("Sending request to URI: {}", request.getURI());

        String currentUserEmail = userService.getCurrentUser();
        log.debug("Received email: '{}'", currentUserEmail);

        if (request.getURI().toString().contains(appConfig.getTodoistTasksApiUrl())) { // TodoistHeaderInterceptor

            User user = userRepository.findByEmail(currentUserEmail).orElseThrow(
                    () -> new UserNotFoundException(format("User with email: '%s' not found", currentUserEmail)));

            String userToken = user.getTodoistToken();

            setRequestHeaders(request, userToken);

        } else if (request.getURI().toString().equals(appConfig.getExchangeTodoistTokenUri())) { // TodoistTokenExchangeInterceptor

            request.getHeaders().setContentType(APPLICATION_FORM_URLENCODED);

            String code = authorizationCodeStorage.get(currentUserEmail);

            if (code != null) {

                String requestBody = format(
                        "client_id=%s&client_secret=%s&code=%s&redirect_uri=%s",
                        appConfig.getTodoistClientId(),
                        appConfig.getTodoistClientSecret(),
                        code,
                        appConfig.getTodoistAuthRedirectUri()
                );

                body = requestBody.getBytes(StandardCharsets.UTF_8);

            } else {
                log.error("Authorization code is missing for user: {}", currentUserEmail);
                throw new IllegalArgumentException("Authorization code is missing in session");
            }
        }

        return execution.execute(request, body);
    }

    private void setRequestHeaders(HttpRequest request, String userToken) {

        if (request.getMethod().equals(HttpMethod.POST)) {
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            request.getHeaders().set("X-Request-Id", UUID.randomUUID().toString());
            request.getHeaders().set("Authorization", userToken);

            log.debug("Sending POST request to URI: {}", request.getURI());

        } else if (request.getMethod().equals(HttpMethod.DELETE) || request.getMethod().equals(HttpMethod.GET)) {
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            request.getHeaders().set("Authorization", userToken);

            log.debug("Sending {} request to URI: {}", request.getMethod().name(), request.getURI());
        }

        log.debug("Added {} request headers", request.getHeaders().size());
    }
}
