package io.project.kitchen_assistant.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.dto.todoist.tasks.TaskDTO;
import io.project.kitchen_assistant.dto.todoist.tasks.TodoistTaskRequest;
import io.project.kitchen_assistant.dto.todoist.tasks.TodoistTaskResponse;
import io.project.kitchen_assistant.dto.todoist.tasks.response.Due;
import io.project.kitchen_assistant.exception.TaskNotFoundException;
import io.project.kitchen_assistant.mapper.TaskMapper;
import io.project.kitchen_assistant.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.reset;


@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private AppConfig appConfig;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    @Qualifier("restTemplateForPostToTodoist")
    private RestTemplate restTemplateTodoistApiForPost;

    @Mock
    @Qualifier("restTemplateForGetAndDeleteOnTodoist")
    private RestTemplate restTemplateTodoistApiForGetAndDelete;

    @InjectMocks
    private TaskServiceImpl taskServiceImpl;

    @BeforeEach
    public void setUp() {
        when(appConfig.getTodoistTasksApiUrl()).thenReturn("http://testurl.com/tasks");
    }

    @AfterEach
    public void clear() {
        reset(appConfig, taskMapper, restTemplateTodoistApiForPost, restTemplateTodoistApiForGetAndDelete);
    }

    @Test
    public void testCreate_Success() {
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

        when(restTemplateTodoistApiForPost.exchange(eq("http://testurl.com/tasks"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(TodoistTaskResponse.class)))
                .thenReturn(ResponseEntity.ok(taskResponse));

        TaskDTO expectedTaskDTO = new TaskDTO("12345", "Test Task", "", due, List.of("Food", "Shopping"));

        when(taskMapper.toDTO(taskResponse)).thenReturn(expectedTaskDTO);

        TaskDTO result = taskServiceImpl.create(taskRequest);

        assertNotNull(result);
        assertEquals(expectedTaskDTO, result);
        verify(taskMapper, times(1)).toDTO(taskResponse);
    }

    @Test
    public void testCreate_ErrorResponse() {
        TodoistTaskRequest taskRequest = new TodoistTaskRequest();
        taskRequest.setContent("Test Task");

        when(restTemplateTodoistApiForPost.exchange(eq("http://testurl.com/tasks"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(TodoistTaskResponse.class)))
                .thenReturn(ResponseEntity.badRequest().build());

        assertThrows(IllegalArgumentException.class, () -> taskServiceImpl.create(taskRequest));
    }

    @Test
    public void testCreate_NullResponseBody() {
        TodoistTaskRequest taskRequest = new TodoistTaskRequest();
        taskRequest.setContent("Test Task");

        when(restTemplateTodoistApiForPost.exchange(eq("http://testurl.com/tasks"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(TodoistTaskResponse.class)))
                .thenReturn(ResponseEntity.ok().body(null));

        assertThrows(IllegalArgumentException.class, () -> taskServiceImpl.create(taskRequest));
    }

    @Test
    public void testCreate_HttpClientErrorException() {
        TodoistTaskRequest taskRequest = new TodoistTaskRequest();
        taskRequest.setContent("Test Task");

        when(restTemplateTodoistApiForPost.exchange(eq("http://testurl.com/tasks"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(TodoistTaskResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(ResponseStatusException.class, () -> taskServiceImpl.create(taskRequest));
    }

    @Test
    public void testCreate_UnexpectedError() {
        TodoistTaskRequest taskRequest = new TodoistTaskRequest();
        taskRequest.setContent("Test Task");

        when(restTemplateTodoistApiForPost.exchange(eq("http://testurl.com/tasks"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(TodoistTaskResponse.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(RuntimeException.class, () -> taskServiceImpl.create(taskRequest));
    }

    @Test
    public void testGetById_Success() {
        String taskId = "12345";
        String url = "http://testurl.com/tasks/12345";

        TodoistTaskResponse taskResponse = new TodoistTaskResponse();
        taskResponse.setId("12345");
        taskResponse.setContent("content");
        taskResponse.setDue(new Due());
        taskResponse.setLabels(List.of());

        when(restTemplateTodoistApiForGetAndDelete.exchange(eq(url),
                eq(HttpMethod.GET),
                isNull(),
                eq(TodoistTaskResponse.class)))
                .thenReturn(ResponseEntity.ok(taskResponse));

        TaskDTO expectedTaskDTO = new TaskDTO("12345", "content", "", new Due(), List.of());

        when(taskMapper.toDTO(taskResponse)).thenReturn(expectedTaskDTO);

        TaskDTO result = taskServiceImpl.getById(taskId);

        assertNotNull(result);
        assertEquals(expectedTaskDTO, result);
        verify(taskMapper, times(1)).toDTO(taskResponse);
    }

//    @Test
//    public void testGetById_InvalidTaskId() {
//        String taskId = "invalid_id";
//
//        when(restTemplateTodoistApiForGetAndDelete.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(TodoistTaskResponse.class)))
//                .thenThrow(new IllegalArgumentException(HttpStatus.BAD_REQUEST));
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            taskServiceImpl.getById(taskId);
//        });
//
//        assertEquals("Invalid task ID provided: invalid_id", exception.getMessage());
//    }

    @Test
    public void testGetById_TaskNotFound() {
        String taskId = "12345";
        String url = "http://testurl.com/tasks/12345";

        when(restTemplateTodoistApiForGetAndDelete.exchange(eq(url),
                eq(HttpMethod.GET),
                isNull(),
                eq(TodoistTaskResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskServiceImpl.getById(taskId));
//        assertEquals("Task with ID 12345 not found.", exception.getMessage());
    }

    @Test
    public void testGetById_BadRequest() {
        String taskId = "invalid_id";
        String url = String.format("%s/%s", appConfig.getTodoistTasksApiUrl(), taskId);

        when(restTemplateTodoistApiForGetAndDelete.exchange(eq(url),
                eq(HttpMethod.GET), isNull(), eq(TodoistTaskResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> taskServiceImpl.getById(taskId));

//        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Error during API call", exception.getReason());
    }

    @Test
    public void testGetById_UnexpectedError() {
        String taskId = "12345";
        String url = "http://testurl.com/tasks/12345";

        when(restTemplateTodoistApiForGetAndDelete.exchange(eq(url),
                eq(HttpMethod.GET), isNull(), eq(TodoistTaskResponse.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskServiceImpl.getById(taskId));
        assertEquals("Unexpected error occurred while receiving task: Unexpected error. java.lang.RuntimeException: Unexpected error",
                exception.getMessage());
    }

//    @Test
//    public void testGetById_UnexpectedError2() {
//        String taskId = "12345";
//        String url = "http://testurl.com/tasks/12345";
//
//        when(restTemplateTodoistApiForGetAndDelete.exchange(eq(url),
//                eq(HttpMethod.GET), isNull(), eq(TodoistTaskResponse.class)))
//                .thenThrow(new RuntimeException("Unexpected error"));
//
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskServiceImpl.getById(taskId));
//        assertEquals("Unexpected error occurred while receiving task: Unexpected error. java.lang.RuntimeException: Unexpected error",
//                exception.getMessage());
//    }
}
