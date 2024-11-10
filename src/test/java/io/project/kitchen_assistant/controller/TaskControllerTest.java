package io.project.kitchen_assistant.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.project.kitchen_assistant.dto.todoist.tasks.TaskDTO;
import io.project.kitchen_assistant.dto.todoist.tasks.TodoistTaskRequest;
import io.project.kitchen_assistant.mapper.TaskMapper;
import io.project.kitchen_assistant.util.FileReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Testcontainers
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private WebApplicationContext wac;

    private TodoistTaskRequest taskRequest;
    private TodoistTaskRequest taskRequest2;

    private static final List<String> idsForClear = new ArrayList<>();

    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    static {
        postgresContainer.start();
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        try {
            String fixtureForRequest1 = FileReader.readFixture("test-request-1.json");
            String fixtureForRequest2 = FileReader.readFixture("test-request-2.json");

            taskRequest = om.readValue(fixtureForRequest1, TodoistTaskRequest.class);
            log.info("TaskRequest: '{}'", taskRequest);

            taskRequest2 = om.readValue(fixtureForRequest2, TodoistTaskRequest.class);
            log.info("TaskRequest2: '{}'", taskRequest2);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void clearTestData() throws Exception {
        for (String id : idsForClear) {
            mockMvc.perform(delete("/api/v1/tasks/{id}", id))
                    .andExpect(status().isNoContent());
        }
    }

    @Test
    public void testCreate_Created_Success() throws Exception {
        String result = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(taskRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        TaskDTO actualDto = om.readValue(result, TaskDTO.class);

        assertThat(actualDto.getContent()).isEqualTo("Рецепт пирога");
        assertThat(actualDto.getDescription()).isEqualTo("Список игредиентов для пирога");
        assertThat(actualDto.getLabels().size()).isEqualTo(2);
        assertTrue(actualDto.getLabels().contains("Shopping"));

        idsForClear.add(actualDto.getId());
    }

    @Test
    public void testCreate_BadRequest_InvalidContent() throws Exception {
        TodoistTaskRequest request = new TodoistTaskRequest("", "test description", List.of(),"2024-11-03", "2024-11-03");

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAll() throws Exception {
        var result = mockMvc.perform(get("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        log.debug("Response Body: {}", body);

        assertThatJson(body).isArray();
    }

    @Test
    public void testShow_Ok_CreateAndShowSuccess() throws Exception {
        String result = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(taskRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        TaskDTO actualDto = om.readValue(result, TaskDTO.class);

        var request = get("/api/v1/tasks/{id}", actualDto.getId());

        var body = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("content").isEqualTo(actualDto.getContent()),
                v -> v.node("description").isEqualTo(actualDto.getDescription())
        );

        idsForClear.add(actualDto.getId());
    }

    @Test
    public void testShow_NotFound_NonExistentId() throws Exception {
        String nonExistentId = "999";
        var request = get("/api/v1/tasks/{id}", nonExistentId);

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().string(
                        String.format("Task with ID %s not found.", nonExistentId)));
    }

    @Test
    public void testDelete_NoContent_NewlyCreatedId() throws Exception {
        String result = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(taskRequest2)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        TaskDTO actualDto = om.readValue(result, TaskDTO.class);

        var request = delete("/api/v1/tasks/{id}", actualDto.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        var requestForGet2 = get("/api/v1/tasks/{id}", actualDto.getId());

        mockMvc.perform(requestForGet2)
                .andExpect(status().isNotFound())
                .andExpect(content().string(
                        String.format("Task with ID %s not found.", actualDto.getId())));
    }

    @Test
    public void testDelete_BadRequest_InvalidId() throws Exception {
        String invalidId = "0";
        var request = delete("/api/v1/tasks/{id}", invalidId);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }
}
