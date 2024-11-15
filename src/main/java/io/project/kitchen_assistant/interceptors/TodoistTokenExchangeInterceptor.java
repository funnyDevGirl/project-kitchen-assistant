package io.project.kitchen_assistant.interceptors;

import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.config.AuthorizationCodeContext;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Component
@AllArgsConstructor
public class TodoistTokenExchangeInterceptor implements ClientHttpRequestInterceptor {

    private final AppConfig appConfig;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        request.getHeaders().setContentType(APPLICATION_FORM_URLENCODED);


        if (request.getURI().toString().equals("https://todoist.com/oauth/access_token")) {

            String requestBody = format(
                    "client_id=%s&client_secret=%s&code=%s&redirect_uri=%s",
                    appConfig.getTodoistClientId(),
                    appConfig.getTodoistClientSecret(),
                    AuthorizationCodeContext.getAuthorizationCode(),
                    appConfig.getTodoistAuthRedirectUri());

            body = requestBody.getBytes(StandardCharsets.UTF_8);
        }

        return execution.execute(request, body);
    }
}
