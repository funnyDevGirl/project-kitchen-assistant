package io.project.kitchen_assistant.utils;

import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class UserUtilsTest {

    private UserUtils userUtils;

    private UserRepository mockUserRepository;

    private String userEmail;

    private long userId;

    private User user;

    @BeforeEach
    public void setUp() {
        mockUserRepository = Mockito.mock(UserRepository.class);
        userUtils = new UserUtils(mockUserRepository);

        userEmail = "test@example.com";
        userId = 1L;

        user = new User();
        user.setEmail(userEmail);
    }

    @Test
    public void testGetCurrentUserEmailWhenAuthenticated() {
        // Mock Authentication object
        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken(userEmail, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        // Act
        String actualEmail = userUtils.getCurrentUserEmail();

        // Assert
        assertEquals(userEmail, actualEmail);
    }

    @Test
    public void testGetCurrentUserEmailWhenNotAuthenticated() {
        // Arrange
        SecurityContextHolder.clearContext();

        // Act & Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                userUtils::getCurrentUserEmail);

        assertEquals("User is not authenticated", thrown.getMessage());
    }

    @Test
    public void testIsUserWhenUserIsAuthenticated() {
        // Mock User
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock Authentication
        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken(userEmail, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        // Act
        boolean result = userUtils.isUser(userId);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testIsUserWhenUserIsNotAuthenticated() {
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        SecurityContextHolder.clearContext();

        // Act & Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> userUtils.isUser(userId));

        assertEquals("User is not authenticated", thrown.getMessage());
    }

    @Test
    public void testIsUserWhenUserDoesNotMatch() {
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));

        // Mock Authentication
        Authentication mockAuthentication = new UsernamePasswordAuthenticationToken(
                "anotheruser@example.com", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        // Act
        boolean result = userUtils.isUser(userId);

        // Assert
        assertFalse(result);
    }
}
