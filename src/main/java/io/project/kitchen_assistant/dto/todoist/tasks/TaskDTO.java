package io.project.kitchen_assistant.dto.todoist.tasks;

import io.project.kitchen_assistant.dto.todoist.tasks.response.Due;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@Schema(description = "TaskDTO's information")
public class TaskDTO {

    @Schema(description = "Task ID", example = "2995104339", type = "string")
    private String id;

    @Schema(description = "Field for Recipe name", example = "Вишневый пирог", type = "string")
    @Size(max = 255, message = "Content must be 255 characters or less")
    private String content;

    @Schema(description = "Description of the task in free form", example = "Ещё не забыть купить еду для кота.",
            type = "string")
    @Size(max = 500, message = "Description must be 500 characters or less")
    private String description;

    @Schema(
            description = "Object representing task due date/time, or null if no date is set.",
            example = "{\"date\":\"2023-12-31\", \"string\":\"tomorrow\", \"is_recurring\":false, \"lang\":\"en\"}",
            nullable = true
    )
    private Due due;

    @ArraySchema(
            schema = @Schema(description = "Label names", type = "string", example = "Food"),
            arraySchema = @Schema(description = "List with label names", example = "[\"Food\", \"Shopping\"]")
    )
    private List<String> labels;
}
