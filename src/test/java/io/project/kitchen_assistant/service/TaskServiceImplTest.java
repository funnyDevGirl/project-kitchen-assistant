package io.project.kitchen_assistant.service;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.dto.todoist.tasks.TaskDTO;
import io.project.kitchen_assistant.dto.todoist.tasks.TodoistTaskRequest;
import io.project.kitchen_assistant.dto.todoist.tasks.TodoistTaskResponse;
import io.project.kitchen_assistant.dto.todoist.tasks.response.Due;
import io.project.kitchen_assistant.exception.TaskNotFoundException;
import io.project.kitchen_assistant.mapper.TaskMapper;
import io.project.kitchen_assistant.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
public class TaskServiceImplTest {
    @Mock
    private AppConfig appConfig;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    @Qualifier("restTemplateForTodoist")
    private RestTemplate restTemplateTodoistApi;

    @InjectMocks
    private TaskServiceImpl taskServiceImpl;

    @BeforeEach
    public void setUp() {
        String mockUrl = "http://testurl.com/tasks";
        when(appConfig.getTodoistTasksApiUrl()).thenReturn(mockUrl);
    }

    @Test
    public void testCreateSuccess() {
        TodoistTaskRequest taskRequest = new TodoistTaskRequest();
        taskRequest.setContent("Test Task");
        taskRequest.setLabels(List.of());
        taskRequest.setDueDate("2024-11-04");
        taskRequest.setDueString("test");

        TodoistTaskResponse taskResponse = new TodoistTaskResponse();
        taskResponse.setId("12345");
        taskResponse.setContent("Test Task");
        Due due = new Due();
        due.setDate(LocalDate.parse("2024-11-04"));
        due.setString("test");
        taskResponse.setDue(due);
        taskResponse.setLabels(List.of("Food", "Shopping"));

        when(restTemplateTodoistApi.exchange(eq(appConfig.getTodoistTasksApiUrl()),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(TodoistTaskResponse.class)))
                .thenReturn(ResponseEntity.ok(taskResponse));

        TaskDTO expectedTaskDTO = new TaskDTO("12345", "Test Task", "", due, List.of("Food", "Shopping"));

        when(taskMapper.toDTO(taskResponse)).thenReturn(expectedTaskDTO);

        TaskDTO result = taskServiceImpl.create(taskRequest);

        assertNotNull(result);
        assertEquals(expectedTaskDTO, result);
        verify(taskMapper).toDTO(taskResponse);
    }

    @Test
    public void testCreateErrorResponse() {
        TodoistTaskRequest taskRequest = new TodoistTaskRequest();
        taskRequest.setContent("Test Task");

        when(restTemplateTodoistApi.exchange(eq(appConfig.getTodoistTasksApiUrl()),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(TodoistTaskResponse.class)))
                .thenReturn(ResponseEntity.badRequest().build());

        assertThrows(IllegalArgumentException.class, () -> taskServiceImpl.create(taskRequest));
    }

    @Test
    public void testCreateNullResponseBody() {
        TodoistTaskRequest taskRequest = new TodoistTaskRequest();
        taskRequest.setContent("Test Task");

        when(restTemplateTodoistApi.exchange(eq(appConfig.getTodoistTasksApiUrl()),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(TodoistTaskResponse.class)))
                .thenReturn(ResponseEntity.ok().body(null));

        assertThrows(IllegalArgumentException.class, () -> taskServiceImpl.create(taskRequest));
    }

    @Test
    public void testCreateHttpClientErrorException() {
        TodoistTaskRequest taskRequest = new TodoistTaskRequest();
        taskRequest.setContent("Test Task");

        when(restTemplateTodoistApi.exchange(eq(appConfig.getTodoistTasksApiUrl()),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(TodoistTaskResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(ResponseStatusException.class, () -> taskServiceImpl.create(taskRequest));
    }

    @Test
    public void testCreateUnexpectedError() {
        TodoistTaskRequest taskRequest = new TodoistTaskRequest();
        taskRequest.setContent("Test Task");

        when(restTemplateTodoistApi.exchange(eq(appConfig.getTodoistTasksApiUrl()),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(TodoistTaskResponse.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(RuntimeException.class, () -> taskServiceImpl.create(taskRequest));
    }

    @Test
    public void testGetByIdSuccess() {
        String taskId = "12345";
        String url = format("%s/%s", appConfig.getTodoistTasksApiUrl(), taskId);

        TodoistTaskResponse taskResponse = new TodoistTaskResponse();
        taskResponse.setId("12345");
        taskResponse.setContent("content");
        taskResponse.setDue(new Due());
        taskResponse.setLabels(List.of());

        when(restTemplateTodoistApi.exchange(eq(url),
                eq(HttpMethod.GET),
                isNull(),
                eq(TodoistTaskResponse.class)))
                .thenReturn(ResponseEntity.ok(taskResponse));

        TaskDTO expectedTaskDTO = new TaskDTO("12345", "content", "", new Due(), List.of());

        when(taskMapper.toDTO(taskResponse)).thenReturn(expectedTaskDTO);

        TaskDTO result = taskServiceImpl.getById(taskId);

        assertNotNull(result);
        assertEquals(expectedTaskDTO, result);
        verify(taskMapper).toDTO(taskResponse);
    }

    @Test
    public void testGetByIdTaskNotFound() {
        String taskId = "12345";
        String url = format("%s/%s", appConfig.getTodoistTasksApiUrl(), taskId);

        when(restTemplateTodoistApi.exchange(eq(url),
                eq(HttpMethod.GET),
                isNull(),
                eq(TodoistTaskResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class,
                () -> taskServiceImpl.getById(taskId));
        assertEquals("Task with ID 12345 not found.", exception.getMessage());
    }

    @Test
    public void testGetByIdBadRequest() {
        String taskId = "invalid_id";
        String url = format("%s/%s", appConfig.getTodoistTasksApiUrl(), taskId);

        when(restTemplateTodoistApi.exchange(eq(url),
                eq(HttpMethod.GET), isNull(), eq(TodoistTaskResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskServiceImpl.getById(taskId));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid task ID provided: invalid_id", exception.getReason());
    }

    @Test
    public void testGetByIdUnexpectedError() {
        String taskId = "12345";
        String url = format("%s/%s", appConfig.getTodoistTasksApiUrl(), taskId);

        when(restTemplateTodoistApi.exchange(eq(url),
                eq(HttpMethod.GET), isNull(), eq(TodoistTaskResponse.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskServiceImpl.getById(taskId));
        assertEquals(format("%s %s",
                        "Unexpected error occurred while receiving task: Unexpected error.",
                        "java.lang.RuntimeException: Unexpected error"),
                exception.getMessage()
        );
    }
}
