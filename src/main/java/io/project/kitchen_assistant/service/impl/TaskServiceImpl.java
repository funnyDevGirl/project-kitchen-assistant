package io.project.kitchen_assistant.service.impl;

import io.project.kitchen_assistant.config.AppConfig;
import io.project.kitchen_assistant.dto.todoist.tasks.TaskDTO;
import io.project.kitchen_assistant.dto.todoist.tasks.TodoistTaskRequest;
import io.project.kitchen_assistant.dto.todoist.tasks.TodoistTaskResponse;
import io.project.kitchen_assistant.exception.TaskNotFoundException;
import io.project.kitchen_assistant.mapper.TaskMapper;
import io.project.kitchen_assistant.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static java.lang.String.format;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    private final AppConfig appConfig;
    private final TaskMapper taskMapper;
    private final RestTemplate restTemplateTodoistApi;

    public TaskServiceImpl(AppConfig appConfig, TaskMapper taskMapper,
                           @Qualifier("restTemplateForTodoist") RestTemplate restTemplateTodoistApi) {
        this.appConfig = appConfig;
        this.taskMapper = taskMapper;
        this.restTemplateTodoistApi = restTemplateTodoistApi;
    }

    public TaskDTO create(TodoistTaskRequest taskRequest) {
        addDefaultLabels(taskRequest);
        ResponseEntity<TodoistTaskResponse> responseEntity = exchangeForPost(taskRequest);

        return Optional.ofNullable(responseEntity.getBody())
                .map(taskMapper::toDTO)
                .map(task -> {
                    log.info("The response from Todoist to create the task: '{}'", responseEntity.getBody());
                    log.info("Task with content '{}' has been created on Todoist. Here is the response: '{}'",
                            taskRequest.getContent(), task);
                    return task;
                })
                .orElseThrow(() -> new IllegalArgumentException(
                        format("Check the request data. Failed to create a task with data: %s", taskRequest)));
    }

    private ResponseEntity<TodoistTaskResponse> exchangeForPost(TodoistTaskRequest taskRequest) {
        try {
            return restTemplateTodoistApi.exchange(
                    appConfig.getTodoistTasksApiUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(taskRequest),
                    TodoistTaskResponse.class
            );

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error occurred while creating task: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during API call", e);

        } catch (Exception e) {
            log.error("Error occurred while creating a task: {}", e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred while creating the task.", e);
        }
    }

    public TaskDTO getById(String id) {
        String url = format("%s/%s", appConfig.getTodoistTasksApiUrl(), id);
        ResponseEntity<TodoistTaskResponse> response = exchangeForGet(id, url);

        return Optional.ofNullable(response.getBody())
                .map(taskMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Task response must not be null"));
    }

    private ResponseEntity<TodoistTaskResponse> exchangeForGet(String id, String url) {
        try {
            return restTemplateTodoistApi.exchange(url, HttpMethod.GET, null, TodoistTaskResponse.class);

        } catch (HttpClientErrorException e) {

            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                throw new TaskNotFoundException(String.format("Task with ID %s not found.", id));

            } else if (HttpStatus.BAD_REQUEST.equals(e.getStatusCode())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid task ID provided: " + id);
            }
            throw new RuntimeException(String.format("Error while retrieving task: %s. %s", e.getMessage(), e));

        } catch (Exception e) {
            throw new RuntimeException(
                    format("Unexpected error occurred while receiving task: %s. %s", e.getMessage(), e));
        }
    }

    public List<TaskDTO> getAll() {
        ResponseEntity<TodoistTaskResponse[]> responseEntity = exchangeForGetAll();

        return Optional.ofNullable(responseEntity.getBody())
                .map(taskArray -> {
                    log.info("{} tasks successfully received.", taskArray.length);
                    return Arrays.stream(taskArray)
                            .map(taskMapper::toDTO)
                            .toList();
                })
                .orElseGet(() -> {
                    log.info("No tasks received, returning an empty list.");
                    return Collections.emptyList();
                });
    }

    private ResponseEntity<TodoistTaskResponse[]> exchangeForGetAll() {
        try {
            return restTemplateTodoistApi.exchange(
                    appConfig.getTodoistTasksApiUrl(),
                    HttpMethod.GET,
                    null,
                    TodoistTaskResponse[].class);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error while receiving tasks: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error during API call.", e);

        } catch (Exception e) {
            log.error("Failed to receive tasks: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred while receiving tasks.", e);
        }
    }

    public void delete(String id) {
        String url = format("%s/%s", appConfig.getTodoistTasksApiUrl(), id);
        ResponseEntity<Void> responseEntity = exchangeForDelete(id, url);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Task with ID {} deleted successfully.", id);
        } else {
            log.warn("Failed to delete task with ID {}: {}", id, responseEntity.getStatusCode());
            throw new ResponseStatusException(responseEntity.getStatusCode(), "Failed to delete task.");
        }
    }

    private ResponseEntity<Void> exchangeForDelete(String id, String url) {
        try {
            return restTemplateTodoistApi.exchange(url, HttpMethod.DELETE, null, Void.class);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error occurred while deleting task: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    format("Invalid ID: '%s' was passed when deleting. %s", id, e));

        } catch (Exception e) {
            log.error("Failed to delete task: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred while deleting task", e);
        }
    }

    private void addDefaultLabels(TodoistTaskRequest taskRequest) {
        taskRequest.setLabels(List.of("Food", "Shopping"));
    }
}
