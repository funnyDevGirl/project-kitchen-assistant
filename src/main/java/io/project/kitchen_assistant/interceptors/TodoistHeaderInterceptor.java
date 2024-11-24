package io.project.kitchen_assistant.interceptors;

import io.project.kitchen_assistant.exception.UserNotAuthenticatedException;
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
import java.util.UUID;
import static java.lang.String.format;

@Slf4j
@AllArgsConstructor
@Component
public class TodoistHeaderInterceptor implements ClientHttpRequestInterceptor {

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        String email = userService.getCurrentUser();

        log.info("Received email: '{}'", email);

        User user = userRepository.findByEmail(email).orElseThrow(
               () -> new UserNotFoundException(format("User with email: '%s' not found", email)));

        String userToken = user.getTodoistToken();

        if (userToken == null) {
            log.warn("The user's token is missing, redirecting it to the Todoist login page.");

            throw new UserNotAuthenticatedException("/api/v1/auth/authorize?redirectUri=/api/v1/recipes");
        }

        setRequestHeaders(request, userToken);
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
