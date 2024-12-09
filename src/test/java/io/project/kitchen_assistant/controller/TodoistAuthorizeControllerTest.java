package io.project.kitchen_assistant.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import io.project.kitchen_assistant.dto.todoist.auth.TodoistToken;
import io.project.kitchen_assistant.model.State;
import io.project.kitchen_assistant.repository.StateRepository;
import io.project.kitchen_assistant.service.TodoistAuthorizeService;
import io.project.kitchen_assistant.utils.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.net.URI;
import java.util.Optional;

public class TodoistAuthorizeControllerTest {
    @InjectMocks
    private TodoistAuthorizeController todoistAuthorizeController;

    @Mock
    private JWTUtils jwtUtils;

    @Mock
    private TodoistAuthorizeService todoistAuthorizeService;

    @Mock
    private StateRepository stateRepository;

    @Mock
    private State savedState;

    @Mock
    private Authentication authentication;

    private Authentication authentication2;

    private MockMvc mockMvc;

    private String email;

    private String validUuid;

    private String validCode;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(todoistAuthorizeController).build();

        email = "test@example.com";
        validUuid = "valid-uuid";
        validCode = "valid-code";

        when(authentication.getName()).thenReturn(email);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        savedState = mock(State.class);
    }

    @Test
    void testAuthorize() {
        // Arrange
        String authorizationUrl = "http://todoist.com/auth?client_id=123&scope=read&state=xyz";

        when(authentication.getName()).thenReturn(email);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(todoistAuthorizeService.buildAuthUrl(email)).thenReturn(authorizationUrl);

        // Act
        RedirectView redirectView = todoistAuthorizeController.authorize(authentication);

        // Assert
        assertNotNull(redirectView);
        assertEquals(authorizationUrl, redirectView.getUrl());
    }

    @Test
    void testAuthorizationCallbackSuccess() {
        // Arrange
        when(stateRepository.findByUuid(validUuid)).thenReturn(Optional.of(savedState));
        when(savedState.getUserEmail()).thenReturn(email);

        when(jwtUtils.buildAuthToken(email))
                .thenReturn(new UsernamePasswordAuthenticationToken(email, null));

        TodoistToken todoistToken = new TodoistToken();
        when(todoistAuthorizeService.exchangeToken(validCode, email)).thenReturn(todoistToken);

        // Act
        ResponseEntity<Void> responseEntity =
                todoistAuthorizeController.authorizationCallback(validCode, validUuid);

        // Assert
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertEquals(URI.create("/api/v1/auth/close"), responseEntity.getHeaders().getLocation());

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testAuthorizationCallbackWithInvalidState() {
        // Arrange
        when(stateRepository.findByUuid(validUuid)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            todoistAuthorizeController.authorizationCallback(validCode, validUuid));

        assertEquals("Invalid state provided. The process has been stopped.",
                exception.getMessage());
    }

    @Test
    void testAuthorizationCallbackWhenTokenExchangeFails() {
        // Arrange
        when(stateRepository.findByUuid(validUuid)).thenReturn(Optional.of(savedState));
        when(savedState.getUserEmail()).thenReturn(email);
        when(jwtUtils.buildAuthToken(email))
                .thenReturn(new UsernamePasswordAuthenticationToken(email, null));

        when(todoistAuthorizeService.exchangeToken(validCode, email)).thenReturn(null);

        // Act
        ResponseEntity<Void> responseEntity =
                todoistAuthorizeController.authorizationCallback(validCode, validUuid);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void testCloseTab() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/close"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserEmailWithAuthenticatedUser() {
        when(authentication.isAuthenticated()).thenReturn(true);

        // Act
        String actualEmail = todoistAuthorizeController.getUserEmail(authentication);

        // Assert
        assertEquals(email, actualEmail, "Email should match the authenticated user's email");
    }

    @Test
    void testGetUserEmailWithNullAuthentication() {
        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
            todoistAuthorizeController.getUserEmail(null));

        assertEquals("User is not authenticated", exception.getMessage());
    }

    @Test
    void testGetUserEmailWithNotAuthenticatedUser() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
            todoistAuthorizeController.getUserEmail(authentication));

        assertEquals("User is not authenticated", exception.getMessage());
    }
}
