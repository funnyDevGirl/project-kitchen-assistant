package io.project.kitchen_assistant.interceptors;

import io.project.kitchen_assistant.component.DataStorage;
import io.project.kitchen_assistant.config.AppConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Slf4j
@Component
@AllArgsConstructor
public class TodoistTokenExchangeInterceptor implements ClientHttpRequestInterceptor {

    private final AppConfig appConfig;
    private final DataStorage authorizationCodeStorage;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        request.getHeaders().setContentType(APPLICATION_FORM_URLENCODED);

        if (request.getURI().toString().equals(appConfig.getExchangeTodoistTokenUri())) {

            String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            log.debug("Current user have email: '{}'", currentUserEmail);

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
}
