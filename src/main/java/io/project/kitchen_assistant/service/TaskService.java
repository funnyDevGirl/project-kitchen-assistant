package io.project.kitchen_assistant.service;

import io.project.kitchen_assistant.dto.todoist.tasks.TaskDTO;
import io.project.kitchen_assistant.dto.todoist.tasks.TodoistTaskRequest;
import java.util.List;

public interface TaskService {

    TaskDTO create(TodoistTaskRequest taskRequest);

    TaskDTO getById(String id);

    List<TaskDTO> getAll();

    void delete(String id);
}
