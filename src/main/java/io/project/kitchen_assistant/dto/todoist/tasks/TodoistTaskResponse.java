package io.project.kitchen_assistant.dto.todoist.tasks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.project.kitchen_assistant.dto.todoist.tasks.response.Due;
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
public class TodoistTaskResponse {
    private String id;

    @JsonProperty("project_id")
    private String projectId;

    private Integer order;
    private String content;
    private String description;

    @JsonProperty("is_completed")
    private Boolean isCompleted;

    @JsonProperty(value = "labels")
    private List<String> labels;

    private Integer priority;

    @JsonProperty("comment_count")
    private Integer commentCount;

    @JsonProperty("creator_id")
    private String creatorId;

    @JsonProperty("created_at")
    private Instant createdAt;

    private Due due;
    private String url;
}
