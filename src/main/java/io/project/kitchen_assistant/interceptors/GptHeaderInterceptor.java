package io.project.kitchen_assistant.interceptors;

import io.project.kitchen_assistant.config.AppConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Component
public class GptHeaderInterceptor implements ClientHttpRequestInterceptor {

    private final AppConfig appConfig;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        request.getHeaders().set("x-folder-id", appConfig.getId());
        request.getHeaders().set("Authorization", appConfig.getIAmToken());

        log.info("Sending request to URI: {}", request.getURI());

        return execution.execute(request, body);
    }
}
