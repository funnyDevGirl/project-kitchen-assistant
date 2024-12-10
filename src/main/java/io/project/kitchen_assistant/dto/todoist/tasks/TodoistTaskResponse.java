package io.project.kitchen_assistant.dto.todoist.tasks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.project.kitchen_assistant.dto.todoist.tasks.response.Due;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.List;

/**
 * Класс для получения задачи из Todoist.
 * Содержит только основную информацию о задаче, поля с null игнорируются.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "TodoistTaskResponse's information")
public class TodoistTaskResponse {

    @Schema(description = "Task ID", example = "2995104339", type = "string")
    private String id;

    @Schema(description = "Task's project ID (read-only)", example = "2203306141", type = "string")
    @JsonProperty("project_id")
    private String projectId;

    @Schema(description = "Position under the same parent or project for top-level tasks (read-only)",
            example = "1", type = "integer")
    private Integer order;

    @Schema(description = "Field for Recipe name", example = "Вишневый пирог", type = "string")
    private String content;

    @Schema(description = "Description of the task in free form", example = "Ещё не забыть купить еду для кота.",
            type = "string")
    private String description;

    @Schema(description = "Flag to mark completed tasks", example = "false", type = "Boolean")
    @JsonProperty("is_completed")
    private Boolean isCompleted;

    @Schema(description = "Label names", example = "[\"Food\", \"Shopping\"]", type = "List<String>")
    @JsonProperty(value = "labels")
    private List<String> labels;

    @Schema(description = "Task priority from 1 (normal, default value) to 4 (urgent)", example = "1", type = "Integer")
    private Integer priority;

    @Schema(description = "Number of task comments (read-only)", example = "18", type = "Integer")
    @JsonProperty("comment_count")
    private Integer commentCount;

    @Schema(description = "The ID of the user who created the task (read-only)", example = "2671355", type = "String")
    @JsonProperty("creator_id")
    private String creatorId;

    @Schema(description = "The date when the task was created (read-only)",
            example = "2019-12-11T22:36:50.000000Z", type = "Instant")
    @JsonProperty("created_at")
    private Instant createdAt;

    @Schema(description = "Object representing task due date/time, or null if no date is set (described below)",
            example = "null", type = "Due")
    private Due due;

    @Schema(description = "URL to access this task in the Todoist web or mobile applications (read-only)",
            example = "https://todoist.com/showTask?id=2995104339", type = "String")
    private String url;
}
