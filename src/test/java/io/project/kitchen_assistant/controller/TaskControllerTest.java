package io.project.kitchen_assistant.controller;

import io.project.kitchen_assistant.container.PostgresContainerManager;
import io.project.kitchen_assistant.dto.todoist.tasks.TaskDTO;
import io.project.kitchen_assistant.dto.todoist.tasks.TodoistTaskRequest;
import io.project.kitchen_assistant.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;

@Testcontainers
@ActiveProfiles("test")
class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private final PostgreSQLContainer<?> postgresContainer =
            PostgresContainerManager.getContainer();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void testCreate() throws Exception {
        TodoistTaskRequest request = new TodoistTaskRequest();
        request.setContent("Sample task");

        TaskDTO responseDTO = TaskDTO.builder()
                .id("1")
                .content("Content")
                .build();

        when(taskService.create(any(TodoistTaskRequest.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Sample task\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        verify(taskService).create(any(TodoistTaskRequest.class));
    }

    @Test
    void testGetAll() throws Exception {
        TaskDTO task1 = TaskDTO.builder()
                .id("1")
                .content("Sample Task1")
                .build();

        TaskDTO task2 = TaskDTO.builder()
                .id("2")
                .content("Sample Task2")
                .build();

        List<TaskDTO> tasks = List.of(task1, task2);

        when(taskService.getAll()).thenReturn(tasks);

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        verify(taskService).getAll();
    }

    @Test
    void testShow() throws Exception {
        String id = "1";

        TaskDTO task = TaskDTO.builder()
                .id(id)
                .content("Sample Task")
                .build();

        when(taskService.getById(anyString())).thenReturn(task);

        mockMvc.perform(get("/api/v1/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.content").value("Sample Task"));

        verify(taskService).getById(id);
    }

    @Test
    void testDelete() throws Exception {
        String id = "1";

        mockMvc.perform(delete("/api/v1/tasks/{id}", id))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(taskService).delete(id);
    }
}
