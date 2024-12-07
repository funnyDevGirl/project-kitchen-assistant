package io.project.kitchen_assistant.service;

import io.project.kitchen_assistant.dto.users.UserDTO;
import io.project.kitchen_assistant.dto.users.UserModificationDTO;
import io.project.kitchen_assistant.exception.UserNotFoundException;
import io.project.kitchen_assistant.mapper.UserMapper;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.RecipeRepository;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private RecipeRepository mockRecipeRepository;

    @Mock
    private UserMapper mockUserMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserModificationDTO modificationDTO;

    private User user;

    private UserDTO userDTO;

    @BeforeEach
    public void setUp() {
        // Arrange
        modificationDTO = new UserModificationDTO(
                "test@example.com", "Chuck", "Norris", "qwerty");

        user = new User();
        user.setId(1L);
        user.setFirstName("Chuck");
        user.setLastName("Norris");
        user.setEmail("test@example.com");
        user.setPasswordDigest(passwordEncoder.encode("qwerty"));

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setFirstName("Chuck");
        userDTO.setLastName("Norris");
    }

    @Test
    public void testCreateUser() {

        when(mockUserMapper.toUser(modificationDTO)).thenReturn(user);
        when(mockUserRepository.save(user)).thenReturn(user);
        when(mockUserMapper.toDto(user)).thenReturn(userDTO);

        // Act
        UserDTO createdUserDTO = userService.create(modificationDTO);

        // Assert
        assertNotNull(createdUserDTO);
        verify(mockUserMapper).toUser(modificationDTO);
        verify(mockUserRepository).save(user);
        verify(mockUserMapper).toDto(user);
    }

    @Test
    public void testUpdateUserWhenUserFound() {
        // Arrange
        Long userId = 1L;

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(mockUserMapper).update(modificationDTO, user);
        when(mockUserRepository.save(user)).thenReturn(user);
        when(mockUserMapper.toDto(user)).thenReturn(userDTO);

        // Act
        UserDTO updatedUserDTO = userService.update(modificationDTO, userId);

        // Assert
        assertNotNull(updatedUserDTO);
        verify(mockUserRepository).findById(userId);
        verify(mockUserMapper).update(modificationDTO, user);
        verify(mockUserRepository).save(user);
    }

    @Test
    public void testUpdateUserWhenUserNotFound() {
        // Arrange
        Long userId = 2L;

        when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.update(modificationDTO, userId));
        verify(mockUserRepository).findById(userId);
    }

    @Test
    void testDeleteUserWithNoRecipesShouldDeleteUserAndRecipes() {
        // Arrange
        Long userId = 1L;

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockRecipeRepository.findAllByUser(user)).thenReturn(List.of());

        // Act
        userService.delete(userId);

        // Assert
        verify(mockRecipeRepository).deleteAll(anyList());
        verify(mockUserRepository).deleteById(userId);
    }

    @Test
    public void testDeleteUserWhenUserNotFound() {
        // Arrange
        Long userId = 2L;

        when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
        verify(mockUserRepository).findById(userId);
        verify(mockUserRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testFindByIdWhenUserFound() {
        // Arrange
        Long userId = 1L;

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockUserMapper.toDto(user)).thenReturn(userDTO);

        // Act
        UserDTO foundUserDTO = userService.findById(userId);

        // Assert
        assertNotNull(foundUserDTO);
        verify(mockUserRepository).findById(userId);
        verify(mockUserMapper).toDto(user);
    }

    @Test
    public void testFindByIdWhenUserNotFound() {
        // Arrange
        Long userId = 2L;

        when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
        verify(mockUserRepository).findById(userId);
    }

    @Test
    public void testFindByEmailWhenUserFound() {
        // Arrange
        String email = "test@example.com";

        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(mockUserMapper.toDto(user)).thenReturn(userDTO);

        // Act
        UserDTO foundUserDTO = userService.findByEmail(email);

        // Assert
        assertNotNull(foundUserDTO);
        verify(mockUserRepository).findByEmail(email);
        verify(mockUserMapper).toDto(user);
    }

    @Test
    public void testFindByEmailWhenUserNotFound() {
        // Arrange
        String email = "UserNotFound@example.com";

        when(mockUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.findByEmail(email));
        verify(mockUserRepository).findByEmail(email);
    }
}
