package io.project.kitchen_assistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.project.kitchen_assistant.container.PostgresContainerManager;
import io.project.kitchen_assistant.dto.users.AuthRequest;
import io.project.kitchen_assistant.dto.users.UserModificationDTO;
import io.project.kitchen_assistant.mapper.UserMapper;
import io.project.kitchen_assistant.model.User;
import io.project.kitchen_assistant.repository.UserRepository;
import io.project.kitchen_assistant.utils.JWTUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JWTUtils jwtUtils;

    private User testUser;

    private final PostgreSQLContainer<?> postgresContainer =
            PostgresContainerManager.getContainer();

    @BeforeEach
    public void setUp() {
        UserModificationDTO modificationDTO = new UserModificationDTO(
                "test@example.com", "Chuck", "Norris", "qwerty");
        testUser = userMapper.toUser(modificationDTO);
        userRepository.save(testUser);
    }

    @AfterEach
    public void clean() {
        userRepository.deleteAll();
    }

    @Test
    public void testCreate() throws Exception {
        AuthRequest authRequest = new AuthRequest("test@example.com", "qwerty");

        var request = post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(authRequest));

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThat(body).isNotEmpty();

        String userEmail = jwtUtils.extractUsername(body);
        assertThat(userEmail).isEqualTo("test@example.com");
    }
}
