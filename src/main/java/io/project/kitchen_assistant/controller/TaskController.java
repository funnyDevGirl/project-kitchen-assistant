package io.project.kitchen_assistant.controller;

import io.project.kitchen_assistant.dto.todoist.tasks.TaskDTO;
import io.project.kitchen_assistant.dto.todoist.tasks.TodoistTaskRequest;
import io.project.kitchen_assistant.handler.ErrorResponse;
import io.project.kitchen_assistant.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/tasks")
@Validated
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Create a new task", description = "Creates a new task with the specified details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDTO.class),
                            examples = {@ExampleObject(value = "{\"id\": \"2995104339\", \"content\": "
                                    + "\"Вишневый пирог\", \"description\": \"Ещё не забыть купить еду для кота.\", "
                                    + "\"due\": null,\"labels\": [\"Foo\", \"Shopping\"]}")})
                    ),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(
                                    value = "{\"code\": \"400\", \"message\": \"Invalid task's content\"}")}))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO create(@Valid @RequestBody TodoistTaskRequest taskRequest) {
        return taskService.create(taskRequest);
    }


    @Operation(summary = "Get all tasks", description = "Retrieves a list of all tasks.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "2 tasks successfully received.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = TaskDTO.class),
                            examples = {@ExampleObject(value = "[{\"id\": \"2995104339\", \"content\": "
                                    + "\"Вишневый пирог\", \"description\": \"Ещё не забыть купить еду для кота.\", "
                                    + "\"due\": null, \"labels\": [\"Food\", \"Shopping\"]},{\"id\": \"2995104340\", "
                                    + "\"content\": \"Сходить в спортзал\", \"description\": \"Сделать растяжку\", "
                                    + "\"due\": {\"date\": \"2023-12-31\", \"string\": \"понедельник\", "
                                    + "\"is_recurring\": false, \"lang\": \"ru\"}, \"labels\": [\"Фитнес\"]}]")}))
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TaskDTO>> getAll() {
        List<TaskDTO> tasks = taskService.getAll();
        return ResponseEntity
                .ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .body(tasks);
    }


    @Operation(summary = "Get a task by ID", description = "Retrieves task details by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDTO.class),
                            examples = {@ExampleObject(value = "{\"id\": \"2995104339\", \"content\": "
                                    + "\"Вишневый пирог\", \"description\": \"Ещё не забыть купить еду для кота.\", "
                                    + "\"due\": null,\"labels\": [\"Foo\", \"Shopping\"]}")})
            ),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(
                                    value = "{\"code\": \"404\", \"message\": \"Task with id '3860245' not found\"}")}))
    })
    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO show(@PathVariable("id") @NotBlank String id) {
        return taskService.getById(id);
    }


    @Operation(summary = "Delete a task", description = "Deletes the task with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully",
                    content = @Content()),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {@ExampleObject(
                                    value = "{\"code\": \"404\", \"message\": \"Task with id '3860245' not found\"}")}))
    })
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") @NotBlank String id) {
        taskService.delete(id);
    }
}
