package io.project.kitchen_assistant.interceptors;

import io.project.kitchen_assistant.config.AppConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GptHeaderInterceptorTest {
    @Mock
    private AppConfig appConfig;

    @Mock
    private HttpRequest request;

    @Mock
    private ClientHttpRequestExecution execution;

    @Mock
    private HttpHeaders headers;

    @InjectMocks
    private GptHeaderInterceptor gptHeaderInterceptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(request.getHeaders()).thenReturn(headers);
    }

    @Test
    void testInterceptWithGptApiUrl() throws IOException, URISyntaxException {
        // Arrange
        String gptApiUrl = "http://gpt.api.url";
        when(appConfig.getGptApiUrl()).thenReturn(gptApiUrl);
        when(request.getURI()).thenReturn(new URI(gptApiUrl));

        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(execution.execute(request, new byte[0])).thenReturn(response);

        // Act
        ClientHttpResponse result = gptHeaderInterceptor.intercept(request, new byte[0], execution);

        // Assert
        assertEquals(result, response);
        verify(request.getHeaders()).setContentType(MediaType.APPLICATION_JSON);
        verify(request.getHeaders()).set("x-folder-id", appConfig.getFolderId());
        verify(request.getHeaders()).set("Authorization", appConfig.getIAmToken());
    }

    @Test
    void testInterceptWithGptTokenUrl() throws Exception {
        // Arrange
        String gptTokenUrl = "http://gpt.token.url";
        when(appConfig.getGptTokenUrl()).thenReturn(gptTokenUrl);
        when(request.getURI()).thenReturn(new URI(gptTokenUrl));

        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(execution.execute(request, new byte[0])).thenReturn(response);

        // Act
        ClientHttpResponse result = gptHeaderInterceptor.intercept(request, new byte[0], execution);

        // Assert
        assertEquals(result, response);
        verify(request.getHeaders()).setContentType(MediaType.APPLICATION_JSON);
        verify(request.getHeaders(), never()).set(eq("x-folder-id"), anyString());
        verify(request.getHeaders(), never()).set(eq("Authorization"), anyString());
    }
}
