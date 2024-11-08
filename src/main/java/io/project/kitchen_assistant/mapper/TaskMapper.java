package io.project.kitchen_assistant.mapper;

import io.project.kitchen_assistant.dto.todoist.tasks.TaskDTO;
import io.project.kitchen_assistant.dto.todoist.tasks.TodoistTaskResponse;
import io.project.kitchen_assistant.dto.todoist.tasks.response.Due;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import java.util.Collections;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {
    public TaskDTO toDTO(TodoistTaskResponse taskResponse) {

        if (taskResponse == null) {
            throw new IllegalArgumentException("Task response must not be null");
        }

        return TaskDTO.builder()
                .id(taskResponse.getId())
                .content(taskResponse.getContent())
                .description(taskResponse.getDescription())
                .due(taskResponse.getDue())
                .labels(taskResponse.getLabels() != null ? taskResponse.getLabels() : Collections.emptyList())
                .build();
    }
}
