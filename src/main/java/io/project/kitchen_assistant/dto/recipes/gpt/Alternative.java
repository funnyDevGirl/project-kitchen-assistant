package io.project.kitchen_assistant.dto.recipes.gpt;

import lombok.Data;

/**
 * Класс предназначен только для парсинга ответа от GPT (рецепты).
 */
@Data
public class Alternative {
    private Message message;
    private String status;
}
