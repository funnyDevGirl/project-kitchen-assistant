package io.project.kitchen_assistant.controller;

import io.project.kitchen_assistant.dto.todoist.tasks.TaskDTO;
import io.project.kitchen_assistant.dto.todoist.tasks.TodoistTaskRequest;
import io.project.kitchen_assistant.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/tasks")
@Validated
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO create(@Valid @RequestBody TodoistTaskRequest taskRequest) {
        return taskService.create(taskRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TaskDTO>> getAll() {
        List<TaskDTO> tasks = taskService.getAll();
        return ResponseEntity
                .ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .body(tasks);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO show(@PathVariable @NotBlank String id) {
        return taskService.getById(id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @NotBlank String id) {
        taskService.delete(id);
    }
}
