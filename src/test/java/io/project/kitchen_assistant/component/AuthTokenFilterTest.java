package io.project.kitchen_assistant.component;

import io.project.kitchen_assistant.exception.UserNotFoundException;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.utils.UserUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.anyString;
import static org.mockito.ArgumentMatchers.any;

public class AuthTokenFilterTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserUtils userUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDoFilterWithValidToken() throws Exception {
        // Arrange
        String jwtToken = "valid.jwt.token";
        String email = "test@example.com";
        User user = new User();
        user.setTodoistToken("user-token-and-it-is-not-empty");

        when(request.getHeader("Authorization")).thenReturn(jwtToken);
        when(request.getRequestURI()).thenReturn("/api/v1/tasks");
        when(userUtils.getCurrentUserEmail()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        authTokenFilter.doFilter(request, response, chain);

        // Assert
        verify(response).setHeader("Authorization", jwtToken);
        verify(chain).doFilter(request, response);
    }

    @Test
    void testDoFilterWithMissingToken() throws Exception {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setTodoistToken(null);

        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/v1/tasks");
        when(userUtils.getCurrentUserEmail()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        authTokenFilter.doFilter(request, response, chain);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void testDoFilterWithUserNotFound() throws Exception {
        // Arrange
        String email = "test@example.com";

        when(request.getHeader("Authorization")).thenReturn("valid.jwt.token");
        when(request.getRequestURI()).thenReturn("/api/v1/tasks");
        when(userUtils.getCurrentUserEmail()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        try {
            authTokenFilter.doFilter(request, response, chain);
        } catch (UserNotFoundException e) {

            // Assert
            assertEquals("User with email: 'test@example.com' not found", e.getMessage());
            verify(chain, never()).doFilter(any(), any());
            return;
        }
        fail("Expected UserNotFoundException was not thrown.");
    }

    @Test
    void testDoFilterWithAuthCloseSuccess() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/v1/auth/close");
        when(response.getWriter()).thenReturn(mock(PrintWriter.class));

        // Act
        authTokenFilter.doFilter(request, response, chain);

        // Assert
        verify(response).setContentType("text/html");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response.getWriter()).write(anyString());
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void testDoFilterWithAuthCloseWhenReadHtmlFileThrowsException() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/v1/auth/close");
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        AuthTokenFilter spyFilter = spy(authTokenFilter);
        doThrow(new IOException("File not found")).when(spyFilter).readHtmlFile("successAuth.html");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                spyFilter.doFilter(request, response, chain)
        );

        // Assert
        assertEquals("java.io.IOException: File not found", exception.getMessage());
        verify(writer, never()).write(anyString());
        verify(chain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
    }

    @Test
    void testReadHtmlFileWithSuccess() throws Exception {
        // Arrange
        String expectedHtml = "<html><body>Success!</body></html>";
        InputStream mockInputStream = new ByteArrayInputStream(expectedHtml.getBytes(StandardCharsets.UTF_8));

        AuthTokenFilter spyFilter = spy(authTokenFilter);

        when(spyFilter.getResourceAsStream("successAuth.html")).thenReturn(mockInputStream);

        // Act
        String result = spyFilter.readHtmlFile("successAuth.html");

        // Assert
        assertEquals(expectedHtml, result);
    }

    @Test
    void testReadHtmlFileWithFileNotFound() {
        // Arrange
        AuthTokenFilter spyFilter = spy(authTokenFilter);
        when(spyFilter.getResourceAsStream("missingFile.html")).thenReturn(null);

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () ->
                spyFilter.readHtmlFile("missingFile.html"));

        assertEquals("Html file was not found: missingFile.html", exception.getMessage());
    }
}
