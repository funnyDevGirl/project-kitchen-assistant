package io.project.kitchen_assistant.dto.todoist.tasks;

import io.project.kitchen_assistant.dto.todoist.tasks.response.Due;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

/**
 * Класс для представления существующей задачи в Todoist.
 * Содержит идентификатор, назначенную дату, метки и информацию о задаче.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDTO {
    private String id;

    @Size(max = 255, message = "Content must be 255 characters or less")
    private String content;

    @Size(max = 500, message = "Description must be 500 characters or less")
    private String description;

    private Due due;
    private List<String> labels;
}
