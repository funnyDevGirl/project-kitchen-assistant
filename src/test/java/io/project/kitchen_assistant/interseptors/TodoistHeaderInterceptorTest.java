package io.project.kitchen_assistant.interseptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.interceptors.TodoistHeaderInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import java.io.IOException;
import static org.mockito.Mockito.*;

public class TodoistHeaderInterceptorTest {

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private TodoistHeaderInterceptor interceptor;

    private ClientHttpRequestExecution execution;
    private String testUrl = "http://testurl.com";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        execution = mock(ClientHttpRequestExecution.class);
    }

    @Test
    public void testInterceptPostRequest() throws IOException {
        String requestId = "test-request-id";
        String token = "test-token";

        when(appConfig.getTodoistRequestId()).thenReturn(requestId);
        when(appConfig.getTodoistApiToken()).thenReturn(token);

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.POST, testUrl);

        ClientHttpResponse response = interceptor.intercept(request, new byte[0], execution);

        HttpHeaders headers = request.getHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals(requestId, headers.getFirst("X-Request-Id"));
        assertEquals(token, headers.getFirst("Authorization"));

        verify(execution, times(1)).execute(any(), any());
    }

    @Test
    public void testInterceptGetRequest() throws IOException {
        String token = "test-token";

        when(appConfig.getTodoistApiToken()).thenReturn(token);

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, testUrl);

        ClientHttpResponse response = interceptor.intercept(request, new byte[0], execution);

        HttpHeaders headers = request.getHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals(token, headers.getFirst("Authorization"));

        verify(execution, times(1)).execute(any(), any());
    }

    @Test
    public void testInterceptDeleteRequest() throws IOException {
        String token = "test-token";

        when(appConfig.getTodoistApiToken()).thenReturn(token);

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.DELETE, testUrl);

        ClientHttpResponse response = interceptor.intercept(request, new byte[0], execution);

        HttpHeaders headers = request.getHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals(token, headers.getFirst("Authorization"));

        verify(execution, times(1)).execute(any(), any());
    }
}
