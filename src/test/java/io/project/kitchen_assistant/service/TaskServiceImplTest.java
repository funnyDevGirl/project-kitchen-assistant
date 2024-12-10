package io.project.kitchen_assistant.service;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.project.kitchen_assistant.config.AppConfig;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import nl.altindag.log.LogCaptor;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
public class TaskServiceImplTest {
    @Mock
    private AppConfig appConfig;

    @Mock
    private TaskMapper taskMapper;

    private ObjectMapper objectMapper;

    @Mock
    @Qualifier("restTemplateForTodoist")
    private RestTemplate restTemplateTodoistApi;

    @InjectMocks
    private TaskServiceImpl taskServiceImpl;

    private LogCaptor logCaptor;

    @BeforeEach
    public void setUp() {
        String mockUrl = "http://testurl.com/tasks";
        when(appConfig.getTodoistTasksApiUrl()).thenReturn(mockUrl);

        logCaptor = LogCaptor.forClass(TaskServiceImpl.class);
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

        // Act
        TaskDTO result = taskServiceImpl.create(taskRequest);

        // Assert
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

        // Act, Assert
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

        // Act, Assert
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

        // Act, Assert
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

        // Act, Assert
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

        // Act
        TaskDTO result = taskServiceImpl.getById(taskId);

        // Assert
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

        // Act, Assert
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

        // Act, Assert
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

        // Act, Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taskServiceImpl.getById(taskId));

        assertEquals(format("%s %s",
                        "Unexpected error occurred while receiving task: Unexpected error.",
                        "java.lang.RuntimeException: Unexpected error"),
                exception.getMessage()
        );
    }

    @Test
    public void testGetByIdThrowsRuntimeException() {
        // Arrange
        String taskId = "12345";
        String url = String.format("%s/%s", appConfig.getTodoistTasksApiUrl(), taskId);

        when(restTemplateTodoistApi.exchange(eq(url), eq(HttpMethod.GET),
                isNull(), eq(TodoistTaskResponse.class)))
                .thenThrow(new HttpClientErrorException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Some internal server error"));

        // Act, Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            taskServiceImpl.getById(taskId));

        assertEquals(format("Error while retrieving task: %s. %s", "500 Some internal server error",
                        "org.springframework.web.client.HttpClientErrorException: 500 Some internal server error"),
                exception.getMessage());
    }

    @Test
    void testGetAllSuccess() {
        TodoistTaskResponse taskResponse = new TodoistTaskResponse();
        taskResponse.setId("12345");
        taskResponse.setContent("content");
        taskResponse.setDue(new Due());
        taskResponse.setLabels(List.of());

        TodoistTaskResponse[] response = new TodoistTaskResponse[1];
        response[0] = taskResponse;

        TaskDTO expectedTaskDTO = new TaskDTO("12345", "content", "", new Due(), List.of());

        for (TodoistTaskResponse dto : response) {
            when(taskMapper.toDTO(dto)).thenReturn(expectedTaskDTO);
        }

        when(restTemplateTodoistApi.exchange(eq(appConfig.getTodoistTasksApiUrl()),
                eq(HttpMethod.GET), isNull(), eq(TodoistTaskResponse[].class)))
                .thenReturn(ResponseEntity.ok(response));

        List<TaskDTO> expectedTaskDTOs = List.of(expectedTaskDTO);

        // Act
        List<TaskDTO> result = taskServiceImpl.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(expectedTaskDTOs, result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllEmptyResponseFirst() {
        TodoistTaskResponse[] response = new TodoistTaskResponse[0];

        when(restTemplateTodoistApi.exchange(eq(appConfig.getTodoistTasksApiUrl()),
                eq(HttpMethod.GET), isNull(), eq(TodoistTaskResponse[].class)))
                .thenReturn(ResponseEntity.ok(response));

        // Act
        List<TaskDTO> result = taskServiceImpl.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllWhenResponseIsNull() {
        when(restTemplateTodoistApi.exchange(eq(appConfig.getTodoistTasksApiUrl()),
                eq(HttpMethod.GET), isNull(), eq(TodoistTaskResponse[].class)))
                .thenReturn(ResponseEntity.ok(null));

        // Act
        List<TaskDTO> result = taskServiceImpl.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        assertTrue(logCaptor.getInfoLogs().contains("No tasks received, returning an empty list."));
    }

    @Test
    void testGetAllThrowsHttpClientError() {
        when(restTemplateTodoistApi.exchange(eq(appConfig.getTodoistTasksApiUrl()),
                eq(HttpMethod.GET), isNull(), eq(TodoistTaskResponse[].class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // Act, Assert
        assertThrows(ResponseStatusException.class,
                () -> taskServiceImpl.getAll());
    }

    @Test
    void testGetAllThrowsHttpServerError() {
        when(restTemplateTodoistApi.exchange(eq(appConfig.getTodoistTasksApiUrl()),
                eq(HttpMethod.GET), isNull(), eq(TodoistTaskResponse[].class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // Act, Assert
        assertThrows(ResponseStatusException.class,
                () -> taskServiceImpl.getAll());
    }

    @Test
    void testGetAllThrowsUnexpectedException() {
        when(restTemplateTodoistApi.exchange(eq(appConfig.getTodoistTasksApiUrl()),
                eq(HttpMethod.GET), isNull(), eq(TodoistTaskResponse[].class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act, Assert
        assertThrows(RuntimeException.class,
                () -> taskServiceImpl.getAll());
    }

    @Test
    void testDeleteSuccess() {
        String id = "12345";
        String url = String.format("%s/%s", appConfig.getTodoistTasksApiUrl(), id);

        when(restTemplateTodoistApi.exchange(eq(url),
                eq(HttpMethod.DELETE), isNull(), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        // Act
        taskServiceImpl.delete(id);

        // Assert
        assertThat(logCaptor.getInfoLogs()).contains("Task with ID 12345 deleted successfully.");
    }

    @Test
    void testDeleteNotFound() {
        String id = "12345";
        String url = String.format("%s/%s", appConfig.getTodoistTasksApiUrl(), id);

        when(restTemplateTodoistApi.exchange(eq(url),
                eq(HttpMethod.DELETE), isNull(), eq(Void.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        // AAct, Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskServiceImpl.delete(id));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Failed to delete task.", exception.getReason());
    }

    @Test
    void testDeleteThrowsHttpClientError() {
        String id = "12345";
        String url = String.format("%s/%s", appConfig.getTodoistTasksApiUrl(), id);

        when(restTemplateTodoistApi.exchange(eq(url),
                eq(HttpMethod.DELETE), isNull(), eq(Void.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // Act, Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskServiceImpl.delete(id));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(Objects.requireNonNull(exception.getReason())
                .contains("Invalid ID: '12345' was passed when deleting."));
    }

    @Test
    void testDeleteThrowsHttpServerError() {
        String id = "12345";
        String url = String.format("%s/%s", appConfig.getTodoistTasksApiUrl(), id);

        when(restTemplateTodoistApi.exchange(eq(url),
                eq(HttpMethod.DELETE), isNull(), eq(Void.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // Act, Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskServiceImpl.delete(id));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(Objects.requireNonNull(exception.getReason())
                .contains("Invalid ID: '12345' was passed when deleting."));
    }

    @Test
    void testDeleteThrowsUnexpectedException() {
        String id = "12345";
        String url = String.format("%s/%s", appConfig.getTodoistTasksApiUrl(), id);

        when(restTemplateTodoistApi.exchange(eq(url),
                eq(HttpMethod.DELETE), isNull(), eq(Void.class)))
                .thenThrow(new RuntimeException("Unexpected error occurred while deleting task"));

        // Act, Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taskServiceImpl.delete(id));

        assertEquals("Unexpected error occurred while deleting task", exception.getMessage());
    }
}
