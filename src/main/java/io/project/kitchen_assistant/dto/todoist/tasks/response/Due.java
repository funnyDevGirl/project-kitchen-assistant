package io.project.kitchen_assistant.dto.todoist.tasks.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Due {
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotBlank
    private String string;

    @JsonProperty(value = "is_recurring", defaultValue = "false")
    private Boolean isRecurring;

    @JsonProperty(defaultValue = "en")
    private String lang;
}
