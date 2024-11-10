package io.project.kitchen_assistant.dto.todoist.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Класс для формирования тела запроса на создание задачи в Todoist.
 * В поле content передается тест, содержащий поле name из класса Recipe.
 * Значения для labels устанавливаются в методе addDefaultLabels(taskRequest) при создании задачи.
 * В description передаются ингредиенты (поле ingredients) из Recipe.
 * Поле Due содержит информацию о дате, на которую назначается задача.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TodoistTaskRequest {
    @NotBlank
    @Size(max = 255, message = "Content must be 255 characters or less")
    private String content;

    @Size(max = 500, message = "Description must be 500 characters or less")
    private String description;

    private List<String> labels;

    @JsonProperty("due_date")
    private String dueDate;

    @JsonProperty("due_string")
    private String dueString;
}
