package io.project.kitchen_assistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.project.kitchen_assistant.container.PostgresContainerManager;
import io.project.kitchen_assistant.dto.users.UserDTO;
import io.project.kitchen_assistant.dto.users.UserModificationDTO;
import io.project.kitchen_assistant.mapper.UserMapper;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper userMapper;

    private User testUser;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private final PostgreSQLContainer<?> postgresContainer =
            PostgresContainerManager.getContainer();

    @BeforeEach
    public void setUp() {
        UserModificationDTO modificationDTO = new UserModificationDTO(
                "test@example.com", "Chuck", "Norris", "qwerty");
        testUser = userMapper.toUser(modificationDTO);
        userRepository.save(testUser);

        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @AfterEach
    public void clean() {
        userRepository.deleteAll();
    }

    @Test
    public void testCreate() throws Exception {
        UserModificationDTO modificationDTO = new UserModificationDTO(
                "test-2@example.com", "Chuck", "Norris", "qwerty");
        User newUser = userMapper.toUser(modificationDTO);

        var request = post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(newUser));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail(newUser.getEmail()).orElseThrow();

        assertThat(user).isNotNull();
        assertThat(user.getFirstName()).isEqualTo(newUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(newUser.getLastName());
        assertThat(user.getEmail()).isEqualTo(newUser.getEmail());
        assertThat(user.getPasswordDigest()).isNotEqualTo(newUser.getPasswordDigest());
    }

    @Test
    public void testCreateWithNotValidData() throws Exception {
        UserDTO dto = userMapper.toDto(testUser);
        dto.setEmail("invalid_email");

        var request = post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetById() throws Exception {
        var request = get("/api/v1/users/{id}", testUser.getId())
                .with(jwt());

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("id").isEqualTo(testUser.getId())
        );
    }

    @Test
    public void testShowCurrentUser() throws Exception {
        var request = get("/api/v1/users/current")
                .with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("id").isEqualTo(testUser.getId())
        );
    }

    @Test
    public void testUpdate() throws Exception {
        UserDTO dto = userMapper.toDto(testUser);

        dto.setFirstName("NewName");
        dto.setLastName("NewLastName");

        var request = put("/api/v1/users/{id}", testUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        User user = userRepository.findById(testUser.getId()).orElseThrow();

        assertThat(user.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(user.getLastName()).isEqualTo(dto.getLastName());
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete("/api/v1/users/{id}", testUser.getId()).with(token);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(testUser.getId())).isEqualTo(false);
    }
}
