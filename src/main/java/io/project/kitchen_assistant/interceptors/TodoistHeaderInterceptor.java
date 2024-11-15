package io.project.kitchen_assistant.interceptors;

import io.project.kitchen_assistant.config.AppConfig;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class TodoistHeaderInterceptor implements ClientHttpRequestInterceptor {

    private final AppConfig appConfig;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        if (request.getMethod().equals(HttpMethod.POST)) {
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            request.getHeaders().set("X-Request-Id", UUID.randomUUID().toString());
            request.getHeaders().set("Authorization", appConfig.getTodoistApiToken());

            log.info("Sending POST request to URI: {}", request.getURI());
            log.info("Outgoing request headers: {}", request.getHeaders());

        } else if (request.getMethod().equals(HttpMethod.DELETE) || request.getMethod().equals(HttpMethod.GET)) {
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            request.getHeaders().set("Authorization", appConfig.getTodoistApiToken());

            log.info("Sending {} request to URI: {}", request.getMethod().name(), request.getURI());
        }

        return execution.execute(request, body);
    }
}
