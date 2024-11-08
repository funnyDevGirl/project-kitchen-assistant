package io.project.kitchen_assistant.dto.todoist.tasks.response;

import lombok.Data;

// Этот класс не нужен, если передавать null для продолжительности
@Data
public class Duration {
    private Integer amount;
    private String unit;
}
