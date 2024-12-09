package io.project.kitchen_assistant.mapper;

import io.project.kitchen_assistant.dto.todoist.tasks.TaskDTO;
import io.project.kitchen_assistant.dto.todoist.tasks.TodoistTaskResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TaskMapperTest {

    @InjectMocks
    private TaskMapperImpl taskMapper;

    @Test
    void testToDTOWithShouldMapTodoistTaskResponseToTaskDTO() {
        TodoistTaskResponse taskResponse = new TodoistTaskResponse();
        taskResponse.setId("1");
        taskResponse.setContent("Sample task");
        taskResponse.setDescription("This is a sample task description.");
        taskResponse.setDue(null);
        taskResponse.setLabels(Arrays.asList("label1", "label2"));

        TaskDTO taskDTO = taskMapper.toDTO(taskResponse);

        assertThat(taskDTO).isNotNull();
        assertThat(taskDTO.getId()).isEqualTo(taskResponse.getId());
        assertThat(taskDTO.getContent()).isEqualTo(taskResponse.getContent());
        assertThat(taskDTO.getDescription()).isEqualTo(taskResponse.getDescription());
        assertThat(taskDTO.getDue()).isEqualTo(taskResponse.getDue());
        assertThat(taskDTO.getLabels()).isEqualTo(taskResponse.getLabels());
    }

    @Test
    void testToDTOWithShouldThrowIllegalArgumentExceptionWhenTaskResponseIsNull() {
        assertThrows(IllegalArgumentException.class, () -> taskMapper.toDTO(null));
    }

    @Test
    void testToDTOWithShouldHandleEmptyLabels() {
        TodoistTaskResponse taskResponse = new TodoistTaskResponse();
        taskResponse.setId("1");
        taskResponse.setContent("Sample task");
        taskResponse.setDescription("This is a sample task description.");
        taskResponse.setDue(null);
        taskResponse.setLabels(null);

        TaskDTO taskDTO = taskMapper.toDTO(taskResponse);

        assertThat(taskDTO).isNotNull();
        assertThat(taskDTO.getLabels()).isEmpty();
    }
}
