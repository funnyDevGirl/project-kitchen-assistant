package io.project.kitchen_assistant.service;

import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.service.impl.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class CustomUserDetailsServiceTest {

    private CustomUserDetailsService customUserDetailsService;
    private UserRepository mockUserRepository;

    @BeforeEach
    public void setUp() {
        mockUserRepository = Mockito.mock(UserRepository.class);
        customUserDetailsService = new CustomUserDetailsService(mockUserRepository);
    }

    @Test
    public void testLoadUserByUsernameUserFound() {
        // Arrange
        String email = "test@example.com";
        User mockUser = Mockito.mock(User.class);
        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        when(mockUser.getEmail()).thenReturn(email);

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(mockUser, userDetails);
    }

    @Test
    public void testLoadUserByUsernameUserNotFound() {
        // Arrange
        String email = "UserNotFound@example.com";
        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(email));

        assertEquals(format("User with email '%s' not found", email), thrown.getMessage());
    }
}

