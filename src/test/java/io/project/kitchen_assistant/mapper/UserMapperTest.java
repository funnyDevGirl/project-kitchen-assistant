package io.project.kitchen_assistant.mapper;

import io.project.kitchen_assistant.dto.users.UserDTO;
import io.project.kitchen_assistant.dto.users.UserModificationDTO;
import io.project.kitchen_assistant.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserMapperImpl userMapper;

    @Test
    void testToUserShouldEncryptPassword() {
        UserModificationDTO dto = new UserModificationDTO();
        dto.setPassword("plainPassword");
        dto.setFirstName("Chuck");
        dto.setLastName("Norris");
        dto.setEmail("test@example.com");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        User user = userMapper.toUser(dto);

        assertThat(user).isNotNull();
        assertThat(user.getPasswordDigest()).isEqualTo("encodedPassword");
        assertThat(user.getFirstName()).isEqualTo("Chuck");
        assertThat(user.getLastName()).isEqualTo("Norris");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testToDtoShouldMapUserToUserDTO() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Chuck");
        user.setLastName("Norris");
        user.setEmail("test@example.com");
        user.setCreatedAt(LocalDate.now());

        UserDTO userDTO = userMapper.toDto(user);

        assertThat(userDTO).isNotNull();
        assertThat(userDTO.getId()).isEqualTo(user.getId());
        assertThat(userDTO.getEmail()).isEqualTo(user.getEmail());
        assertThat(userDTO.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(userDTO.getLastName()).isEqualTo(user.getLastName());
        assertThat(userDTO.getCreatedAt()).isEqualTo(user.getCreatedAt());
    }

    @Test
    void testUpdateShouldUpdateUserFields() {
        UserModificationDTO dto = new UserModificationDTO();
        dto.setFirstName("Chuck");
        dto.setLastName("Norris");
        dto.setEmail("test@example.com");

        User user = new User();
        user.setFirstName("OldName");
        user.setLastName("OldLastName");
        user.setEmail("old.email@example.com");
        user.setPasswordDigest("qwerty");

        userMapper.update(dto, user);

        assertThat(user.getFirstName()).isEqualTo("Chuck");
        assertThat(user.getLastName()).isEqualTo("Norris");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPasswordDigest()).isEqualTo("qwerty");
    }

    @Test
    void testSetUserFieldsShouldSetUserFields() {
        UserModificationDTO dto = new UserModificationDTO();
        dto.setFirstName("NewName");
        dto.setEmail("new.email@example.com");

        User user = new User();

        userMapper.setUserFields(dto, user);

        assertThat(user.getFirstName()).isEqualTo("NewName");
        assertThat(user.getEmail()).isEqualTo("new.email@example.com");
        assertThat(user.getLastName()).isNull();
    }
}
