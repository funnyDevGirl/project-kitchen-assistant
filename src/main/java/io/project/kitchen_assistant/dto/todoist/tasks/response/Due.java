package io.project.kitchen_assistant.dto.todoist.tasks.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * Класс предназначен для парсинга ответа от Todoist (TodoistTaskResponse).
 * Содержит информацию о дате, на которую назначается задача.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Due information")
public class Due {

    @Schema(description = "Date in format YYYY-MM-DD", example = "2024-12-01", type = "string", format = "date")
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Schema(description = "Human defined date in arbitrary format", example = "tomorrow at 12", type = "string")
    @NotBlank
    private String string;

    @Schema(description = "Whether the task has a recurring due date.", example = "false", type = "boolean")
    @JsonProperty(value = "is_recurring", defaultValue = "false")
    private Boolean isRecurring;

    @Schema(description = "IETF language tag defining what language filter is written in, "
            + "if differs from default English", example = "en", type = "string")
    @JsonProperty(defaultValue = "en")
    private String lang;
}
