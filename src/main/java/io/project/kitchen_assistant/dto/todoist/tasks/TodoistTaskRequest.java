package io.project.kitchen_assistant.dto.todoist.tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Класс для формирования тела запроса на создание задачи в Todoist.
 * В поле content передается текст, содержащий поле name из класса Recipe.
 * Значения для labels устанавливаются в методе addDefaultLabels(taskRequest) при создании задачи.
 * В description передаются ингредиенты (поле ingredients) из Recipe.
 * Поле Due содержит информацию о дате, на которую назначается задача.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "TodoistTaskRequest's information")
public class TodoistTaskRequest {

    @Schema(description = "Field for Recipe name", example = "Вишневый пирог", type = "string")
    @NotBlank
    @Size(max = 255, message = "Content must be 255 characters or less")
    private String content;

    @Schema(description = "Description of the task in free form", example = "Ещё не забыть купить еду для кота.",
            type = "string")
    @Size(max = 500, message = "Description must be 500 characters or less")
    private String description;

    @ArraySchema(
            schema = @Schema(description = "Label names", type = "string", example = "Food"),
            arraySchema = @Schema(description = "List with label names", example = "[\"Food\", \"Shopping\"]")
    )
    private List<String> labels;

    @Schema(description = "Date in format YYYY-MM-DD", example = "2024-09-01", type = "string")
    @JsonProperty("due_date")
    private String dueDate;

    @Schema(description = "Human defined date in arbitrary format", example = "tomorrow at 12", type = "string")
    @JsonProperty("due_string")
    private String dueString;
}
