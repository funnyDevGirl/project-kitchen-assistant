package io.project.kitchen_assistant.interceptors;

import io.project.kitchen_assistant.component.DataStorage;
import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.exception.UserNotFoundException;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.utils.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

public class TodoistHeaderInterceptorTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private DataStorage authorizationCodeStorage;

    @Mock
    private UserUtils userUtils;

    @Mock
    private AppConfig appConfig;

    @Mock
    private HttpRequest request;

    @Mock
    private ClientHttpRequestExecution execution;

    @Mock
    private HttpHeaders headers;

    @InjectMocks
    private TodoistHeaderInterceptor todoistHeaderInterceptor;

    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(request.getHeaders()).thenReturn(headers);
    }

    @Test
    void testInterceptWithTodoistTasksApiUrlWithPost() throws Exception {
        // Arrange
        String email = "test@example.com";
        String userToken = "user-token";

        when(userUtils.getCurrentUserEmail()).thenReturn(email);
        when(appConfig.getTodoistTasksApiUrl()).thenReturn("http://todoist.api/tasks");
        when(request.getURI()).thenReturn(new URI("http://todoist.api/tasks"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);

        User user = new User();
        user.setTodoistToken(userToken);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(execution.execute(request, new byte[0])).thenReturn(response);

        // Act
        ClientHttpResponse result = todoistHeaderInterceptor.intercept(request, new byte[0], execution);

        // Assert
        assertEquals(response, result);
        verify(headers).setContentType(MediaType.APPLICATION_JSON);
        verify(headers).set("Authorization", userToken);
        verify(headers).set(eq("X-Request-Id"), anyString());
    }

    @Test
    void testInterceptWithTodoistTasksApiUrlWithDelete() throws Exception {
        // Arrange
        String email = "test@example.com";
        String userToken = "user-token";

        when(userUtils.getCurrentUserEmail()).thenReturn(email);
        when(appConfig.getTodoistTasksApiUrl()).thenReturn("http://todoist.api/tasks");
        when(request.getURI()).thenReturn(new URI("http://todoist.api/tasks"));
        when(request.getMethod()).thenReturn(HttpMethod.DELETE);

        User user = new User();
        user.setTodoistToken(userToken);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(execution.execute(request, new byte[0])).thenReturn(response);

        // Act
        ClientHttpResponse result = todoistHeaderInterceptor.intercept(request, new byte[0], execution);

        // Assert
        assertEquals(response, result);
        verify(headers).setContentType(MediaType.APPLICATION_JSON);
        verify(headers).set("Authorization", userToken);
    }

    @Test
    void testInterceptUserNotFound() throws Exception {
        // Arrange
        String email = "test@example.com";
        when(userUtils.getCurrentUserEmail()).thenReturn(email);
        when(appConfig.getTodoistTasksApiUrl()).thenReturn("http://todoist.api/tasks");
        when(request.getURI()).thenReturn(new URI("http://todoist.api/tasks"));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(UserNotFoundException.class, () ->
                todoistHeaderInterceptor.intercept(request, new byte[0], execution));

        assertEquals("User with email: 'test@example.com' not found", exception.getMessage());
        verify(execution, never()).execute(any(), any());
    }

    @Test
    void testInterceptExecutionError() throws Exception {
        // Arrange
        String email = "test@example.com";
        String userToken = "user-token";

        when(userUtils.getCurrentUserEmail()).thenReturn(email);
        when(appConfig.getTodoistTasksApiUrl()).thenReturn("http://todoist.api/tasks");
        when(request.getURI()).thenReturn(new URI("http://todoist.api/tasks"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.getTodoistToken()).thenReturn(userToken);

        when(execution.execute(request, new byte[0])).thenThrow(new RuntimeException("Execution failed"));

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () ->
                todoistHeaderInterceptor.intercept(request, new byte[0], execution)
        );

        // Assert
        assertEquals("Error executing request", exception.getMessage());
        verify(execution).execute(request, new byte[0]);
    }
}
