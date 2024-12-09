package io.project.kitchen_assistant.service;

import io.project.kitchen_assistant.component.DataStorage;
import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.dto.todoist.auth.TodoistToken;
import io.project.kitchen_assistant.exception.UserNotFoundException;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.service.impl.TodoistAuthorizeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

public class TodoistAuthorizeServiceImplTest {

    @InjectMocks
    private TodoistAuthorizeServiceImpl todoistAuthorizeService;

    @Mock
    private AppConfig appConfig;

    @Mock
    private StateService stateService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DataStorage authorizationCodeStorage;

    @Mock
    @Qualifier("restTemplateForTodoist")
    private RestTemplate restTemplateForGetTodoistToken;

    @Mock
    private TodoistToken mockToken;

    @Mock
    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBuildAuthUrl() {
        // Arrange
        String email = "user@example.com";
        String authorizationUri = "http://todoist.com/auth";
        String clientId = "client123";
        String scope = "data:read";

        when(appConfig.getTodoistAuthorizationUri()).thenReturn(authorizationUri);
        when(appConfig.getTodoistClientId()).thenReturn(clientId);
        when(appConfig.getTodoistScope()).thenReturn(scope);

        // Act
        String authUrl = todoistAuthorizeService.buildAuthUrl(email);

        // Assert
        assertNotNull(authUrl);
        assertTrue(authUrl.contains("client_id=" + clientId));
        assertTrue(authUrl.contains("scope=" + scope));
        assertTrue(authUrl.contains("state="));
        verify(stateService).saveState(anyString(), eq("in_progress"), any(LocalDateTime.class), eq(email));
    }

    @Test
    void testExchangeTokenWhenSuccess() {
        // Arrange
        String code = "authorization_code";
        String email = "test@example.com";
        String tokenValue = "Bearer 123456";
        String authTokenUri = "http://todoist.com/auth_token";

        when(appConfig.getExchangeTodoistTokenUri()).thenReturn(authTokenUri);
        when(mockToken.generateBearer()).thenReturn(tokenValue);

        when(restTemplateForGetTodoistToken.exchange(anyString(),
                eq(HttpMethod.POST), isNull(), eq(TodoistToken.class)))
                .thenReturn(ResponseEntity.ok(mockToken));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(mockUser.getTodoistToken()).thenReturn(tokenValue);

        // Act
        TodoistToken token = todoistAuthorizeService.exchangeToken(code, email);

        // Assert
        assertNotNull(token);
        assertEquals(tokenValue, mockUser.getTodoistToken());
        verify(userRepository).save(mockUser);
        verify(authorizationCodeStorage).remove(email);
        verify(authorizationCodeStorage).save(email, code);
    }

    @Test
    void testExchangeTokenWhenFailure() {
        // Arrange
        String code = "authorization_code";
        String email = "user@example.com";
        String authTokenUri = "http://todoist.com/auth_token";

        when(appConfig.getExchangeTodoistTokenUri()).thenReturn(authTokenUri);

        when(restTemplateForGetTodoistToken.exchange(anyString(),
                eq(HttpMethod.POST), isNull(), eq(TodoistToken.class)))
                .thenReturn(ResponseEntity.status(400).build());

        // Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            todoistAuthorizeService.exchangeToken(code, email));

        // Verify exception message
        assertEquals("Failed to exchange token: 400", exception.getMessage());
        verify(authorizationCodeStorage).save(email, code);
        verify(authorizationCodeStorage, never()).remove(email);
    }

    @Test
    void testExchangeTokenWhenUserNotFound() {
        // Arrange
        String code = "authorization_code";
        String email = "user@example.com";
        String authTokenUri = "http://todoist.com/auth_token";

        when(appConfig.getExchangeTodoistTokenUri()).thenReturn(authTokenUri);

        when(restTemplateForGetTodoistToken.exchange(anyString(),
                eq(HttpMethod.POST), isNull(), eq(TodoistToken.class)))
                .thenReturn(ResponseEntity.ok(mockToken));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
            todoistAuthorizeService.exchangeToken(code, email));

        assertEquals("User with email: 'user@example.com' not found", exception.getMessage());
    }
}
